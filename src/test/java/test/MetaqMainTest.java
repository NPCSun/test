package test;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.util.Assert;

import com.taobao.metamorphosis.client.extension.spring.MetaqTemplate;

public class MetaqMainTest extends BaseJunit4Test{
	@Resource  //自动注入,默认按名称  
    private MetaqTemplate metaqTemplate;  
      
    @Test   //标明是测试方法  
    public void testMetaqTemplateIsNullOrNot() {
    	
    	Assert.notNull(metaqTemplate); 
    }  
}
