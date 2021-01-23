package com.sun.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

import cn.cuiot.dmp.common.base.PageResult;
import cn.cuiot.dmp.common.enums.ResultCode;
import cn.cuiot.dmp.common.exception.BusinessException;
import cn.cuiot.dmp.common.exception.SystemException;
import cn.cuiot.dmp.common.service.BaseServiceImpl;
import cn.cuiot.dmp.product.dao.OnlineDocMapper;
import cn.cuiot.dmp.product.model.dto.OnlineDocDto;
import cn.cuiot.dmp.product.model.entity.OnlineDoc;
import cn.cuiot.dmp.product.service.convert.OnlineDocConvert;

/**
 * @author
 * @date 2020/8/3 0003 11:07
 **/
@Service
public class OnlineDocServiceImpl extends BaseServiceImpl<OnlineDocDto, OnlineDoc, Integer>
		implements OnlineDocService {

	@Resource
	private OnlineDocMapper		onlineDocMapper;

	@Resource
	private OnlineDocConvert	onlineDocConvert;

	@Resource
	// private EsUtil esUtil;
	private EsRestUtil			esRestUtil;
	// private static Client client;
	private RestHighLevelClient	client;

	@PostConstruct
	public void init() {
		super.baseMapper = onlineDocMapper;
		super.baseConvert = onlineDocConvert;

		// client = esUtil.getClient();
		client = esRestUtil.getClient();
	}

	@Override
	public List<OnlineDocDto> getAllNode() {
		return onlineDocMapper.getAllNode();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer createAndIndexDoc(OnlineDocDto onlineDocDto) {
		validateDoc(onlineDocDto);
		// 存储文档信息至mysql
		try {
			this.insertWithoutId(onlineDocDto);
		} catch (DataAccessException e) {
			throw new SystemException(ResultCode.ONLINE_DOC_SQL_ERROR);
		}

		// 索引至es
		indexDoc(onlineDocDto);

		return onlineDocDto.getId();
	}

	private void validateDoc(OnlineDocDto onlineDocDto) {
		if (onlineDocDto.isLeaf()) {
			if (StringUtils.isBlank(onlineDocDto.getUrl())) {
				throw new BusinessException(ResultCode.URL_CANNOT_NULL);
			}
			if (StringUtils.isBlank(onlineDocDto.getContent())) {
				throw new BusinessException(ResultCode.CONTENT_CANNOT_NULL);
			}
		}
	}

	/**
	 * 索引至es
	 * 
	 * @param onlineDocDto
	 */
	private void indexDoc(OnlineDocDto onlineDocDto) {
		if (onlineDocDto.isLeaf()) {
			// 处理content，去掉html标签等
			String content = onlineDocDto.getContent();
			content = content.replace("\n", "");
			content = content.replace("\"", "'");

			AnalyzeRequest analyzeRequest = AnalyzeRequest.buildCustomNormalizer("htmldoc")
					//.addTokenFilter("keyword")
					.addCharFilter("html_strip")
					.build(content);
			try {
				AnalyzeResponse analyzeResponse = client.indices().analyze(analyzeRequest, RequestOptions.DEFAULT);

				content = analyzeResponse.getTokens().get(0).getTerm().replace("\n", "  ");
				onlineDocDto.setContent(content);

			} catch (IOException e) {
				throw new BusinessException(ResultCode.ONLINE_DOC_INDEX_FAIL, e);
			}


			// 索引文档
			byte[] json = JSON.toJSONBytes(onlineDocDto);
			IndexRequest indexRequest = new IndexRequest("htmldoc");
			indexRequest.id(String.valueOf(onlineDocDto.getId())).source(json, XContentType.JSON);

			IndexResponse indexResponse = null;
			try {
				indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
			} catch (IOException e) {
				throw new BusinessException(ResultCode.ONLINE_DOC_INDEX_FAIL, e);
			}

			int successCount = indexResponse.getShardInfo().getSuccessful();
			if (successCount != 1) {
				throw new BusinessException(ResultCode.ONLINE_DOC_INDEX_FAIL);
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer updateAndIndexDoc(OnlineDocDto onlineDocDto) {
		validateDoc(onlineDocDto);
		// 存储文档信息至mysql
		try {
			this.update(onlineDocDto);
		} catch (DataAccessException e) {
			throw new SystemException(ResultCode.ONLINE_DOC_SQL_ERROR);
		}

		// 索引至es
		indexDoc(onlineDocDto);

		return onlineDocDto.getId();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteDoc(Integer id) {
		this.deleteById(id);

		// 删除索引文档
		DeleteRequest deleteRequest = new DeleteRequest("htmldoc", String.valueOf(id));
		try {
			DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			throw new BusinessException(ResultCode.ONLINE_DOC_SQL_ERROR, e);
		}
	}

	@Override
	public PageResult<OnlineDocDto> searchDoc(int currentPage, int pageSize, String queryWord) {
		queryWord = queryWord.trim();

		MatchPhraseQueryBuilder titleQuery = QueryBuilders.matchPhraseQuery("title", queryWord);
		MatchPhraseQueryBuilder contentQuery = QueryBuilders.matchPhraseQuery("content", queryWord);

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should(titleQuery).should(contentQuery);

		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.field("title", 10, 5);
		highlightBuilder.field("content", 10, 5);
		highlightBuilder.preTags("<em>");
		highlightBuilder.postTags("</em>");

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(boolQueryBuilder);
		sourceBuilder.from((currentPage - 1) * pageSize);
		sourceBuilder.size(pageSize);
		sourceBuilder.highlighter(highlightBuilder);

		SearchRequest searchRequest = new SearchRequest("htmldoc");
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse;
		List<OnlineDocDto> dtoList;

		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			if (searchResponse == null) {
				throw new BusinessException(ResultCode.INNER_ERROR);
			}
			SearchHits searchHits = searchResponse.getHits();
			dtoList = new ArrayList<>();
			for (SearchHit searchHit : searchHits) {
				OnlineDocDto docDto = JSON.parseObject(searchHit.getSourceAsString(), OnlineDocDto.class);
				// 获取高亮内容
				String title = getHighlightValue(searchHit, "title");
				if (StringUtils.isNotBlank(title)) {
					docDto.setTitle(title);
				}
				String content = getHighlightValue(searchHit, "content");
				if (StringUtils.isNotBlank(content)) {
					docDto.setContent(content);
				}
				//
				dtoList.add(docDto);
			}

			PageResult<OnlineDocDto> pageResult = new PageResult<>();
			pageResult.setCurrentPage(currentPage);
			pageResult.setPageSize(pageSize);
			pageResult.setList(dtoList);
			pageResult.setTotal(searchHits.getTotalHits().value);
			return pageResult;
		} catch (Exception e) {
			throw new BusinessException(ResultCode.INNER_ERROR, e);
		}
	}

	private String getHighlightValue(SearchHit searchHit, String fieldName) {
		Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
		HighlightField highlightField = highlightFields.get(fieldName);
		if (highlightField != null) {
			Text[] texts = highlightField.getFragments();
			String value = "";
			for (Text text : texts) {
				value += text + "&emsp;";
			}
			return value;
		}
		return null;
	}
}
