package com.sun.soup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.steadystate.css.parser.CSSOMParser;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

/**
 *  只支持简单的样式解析识别
 * Created by sun on 2017/8/25 下午4:10.
 */
public class CSSUtils {

	private static final Map<String, List<CssStyle>> cssClassMap = new HashMap<String, List<CssStyle>>();

	public static boolean parse(String cssfile) {

		FileOutputStream out = null;
		boolean rtn = false;

		try {
			// cssfile accessed as a resource, so must be in the pkg (in src dir).
			InputStream stream = new FileInputStream(cssfile);

			// overwrites and existing file contents
			out = new FileOutputStream("/Users/sun/Downloads/log.txt");

			InputSource source = new InputSource(new InputStreamReader(stream));
			CSSOMParser parser = new CSSOMParser();
			// parse and create a stylesheet composition
			CSSStyleSheet stylesheet = parser.parseStyleSheet(source, null, null);

			//ANY ERRORS IN THE DOM WILL BE SENT TO STDERR HERE!!
			// now iterate through the dom and inspect.

			CSSRuleList ruleList = stylesheet.getCssRules();

			System.out.println("Number of rules: " + ruleList.getLength());

			for (int i = 0; i < ruleList.getLength(); i++) {
				CSSRule rule = ruleList.item(i);
				if (rule instanceof CSSStyleRule) {
					CSSStyleRule styleRule = (CSSStyleRule) rule;
					CSSStyleDeclaration styleDeclaration = styleRule.getStyle();
					//
					List<CssStyle> propertyList = new ArrayList<CssStyle>();
					CssStyle cssStyle;
					for (int j = 0; j < styleDeclaration.getLength(); j++) {
						cssStyle = new CssStyle();
						String property = styleDeclaration.item(j);
						cssStyle.setProperty(property);
						cssStyle.setValue(styleDeclaration.getPropertyCSSValue(property).getCssText());
						propertyList.add(cssStyle);
					}
					//
					String selector = styleRule.getSelectorText();
					System.out.println(selector);
					cssClassMap.put(selector, propertyList);

				}// end of StyleRule instance test
			} // end of ruleList loop

			if (out != null) out.close();
			if (stream != null) stream.close();
			rtn = true;
		} catch (IOException ioe) {
			System.err.println("IO Error: " + ioe);
		} catch (Exception e) {
			System.err.println("Error: " + e);

		}

		return rtn;

	}

	public static void main(String[] args) throws Exception {

		String[] strs = ".markdown-body pre>code".split(" ");
		for(String clas : strs){
			System.out.println(clas);
		}
		System.out.println(Jsoup.clean("sfaslfasfasf开始<a>a</a>技术jlaksfjalsjf", Whitelist.simpleText()));

		if (CSSUtils.parse("/Users/sun/Downloads/github-markdown.css")) {

			System.out.println("Parsing completed OK");

		} else {

			System.out.println("Unable to parse CSS");

		}
	}
}
