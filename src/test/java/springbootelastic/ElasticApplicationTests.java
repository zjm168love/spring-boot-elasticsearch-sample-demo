package springbootelastic;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;
import springbootelastic.domin.Article;
import springbootelastic.domin.Author;
import springbootelastic.domin.Tutorial;
import springbootelastic.elasticrepo.ArticleSearchRepository;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticApplicationTests {

  @Autowired
  private ArticleSearchRepository articleSearchRepository;
  @Autowired
  private ElasticsearchOperations es;
  @Autowired
  private ElasticsearchTemplate elasticsearchTemplate;

  /**
   * 建立索引
   */
  @Test
  public void createIndex() {
    elasticsearchTemplate.deleteIndex(Article.class);
    elasticsearchTemplate.createIndex(Article.class);
    elasticsearchTemplate.putMapping(Article.class);
    elasticsearchTemplate.refresh(Article.class);
  }

  /**
   * 删除索引
   */
  @Test
  public void deleteIndex() {
    elasticsearchTemplate.deleteIndex(Article.class);
  }

  /**
   * 添加data
   */
  @Test
  public void testSaveArticleIndex() {
    Author author = new Author();
    author.setId(1L);
    author.setName("狂扁小朋友");
    author.setRemark("java developer");

    List<Article> articleList = new ArrayList<>();
    articleList.add(new Article(1L, 11, "es1", "elasticsearch is very easy",
      "elasticsearch based on lucene", new Date(), 2L, author));
    articleList.add(new Article(2L, 22, "es1", "elasticsearch is amazing",
      "you can enjoy it", new Date(), 1L, author));
    articleList.add(new Article(3L, 33, "es1", "the release of elasticsearch ",
      "elasticsearch make somebody unhappy", new Date(), 5L, author));
    articleList.add(new Article(4L, 44, "es4", "水电费得到",
      "未入围欧特克框架", new Date(), 0L, author));
    articleList.add(new Article(5L, 55, "es5", "是否时代峻峰呼市多久",
      "鹿盔胜多负少的计划", new Date(), 90L, author));
    articleList.add(new Article(6L, 66, "es6", "二恶而",
      "让他有图有认同与他人", new Date(), 11L, author));
    articleList.add(new Article(7L, 77, "es7", "的范德萨发",
      "手动阀十大歌手发过火根据返回", new Date(), 10L, author));

    articleSearchRepository.save(articleList);
  }


  /**
   * 对所有field都进行搜索search
   */
  @Test
  public void testSearch() {

    String queryString = "的";
    QueryStringQueryBuilder builder = new QueryStringQueryBuilder(queryString);
    Iterable<Article> searchResult = articleSearchRepository.search(builder);
    System.out.println("遍历结果------------------------------");
    Iterator<Article> iterator = searchResult.iterator();
    while (iterator.hasNext()) {
      System.out.println(iterator.next());
    }
  }

  /**
   * 精确查询
   */
  @Test
  public void termQuery() {
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    boolQuery.must(QueryBuilders.termQuery("content", "根据"));
    Iterable<Article> jointResult = articleSearchRepository.search(boolQuery);
    Iterator<Article> iteratorJoint = jointResult.iterator();
    System.out.println("精确查询:");
    while (iteratorJoint.hasNext()) {
      System.out.println(iteratorJoint.next());
    }
    printElasticSearchInfo();
  }

  //useful for debug, print elastic search details
  private void printElasticSearchInfo() {

    System.out.println("--ElasticSearch--");
    Client client = es.getClient();
    Map<String, String> asMap = client.settings().getAsMap();

    asMap.forEach((k, v) -> {
      System.out.println(k + " = " + v);
    });
    System.out.println("--ElasticSearch--");
  }

  /**
   * 分页查询
   */
  @Test
  public void pageQuery() {
    Page<Article> articlePage = articleSearchRepository.findByTitle("es1", new PageRequest(0, 2));
    System.out.println("分页查询:    分页数量: " + articlePage.getTotalPages() + "  查询到的记录数量：" + articlePage.getTotalElements());
    articlePage.forEach(System.out::println);
  }

  /**
   * 联合查询
   */
  @Test
  public void jointQuery() {
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    boolQuery.must(QueryBuilders.termQuery("content", "elasticsearch"));
    boolQuery.must(QueryBuilders.termQuery("content", "make"));
    Iterable<Article> jointResult = articleSearchRepository.search(boolQuery);
    Iterator<Article> iteratorJoint = jointResult.iterator();
    System.out.println("联合查询:");
    while (iteratorJoint.hasNext()) {
      System.out.println(iteratorJoint.next());
    }
  }

  /**
   * 分词测试
   */
  @Test
  public void testIKAnalyze() {
    List<String> searchTermsList = getIkAnalyzeSearchTerms("鹿盔胜多负少");
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    searchTermsList.forEach(o -> {
      boolQuery.should(QueryBuilders.termQuery("content", o));
    });
    Iterable<Article> jointResult = articleSearchRepository.search(boolQuery);
    Iterator<Article> iteratorJoint = jointResult.iterator();
    System.out.println("分词查询:");
    while (iteratorJoint.hasNext()) {
      System.out.println(iteratorJoint.next());
    }
  }


  /**
   * 分词查询
   *
   * @param searchContent
   * @return
   */
  private List<String> getIkAnalyzeSearchTerms(String searchContent) {
    // 调用 IK 分词分词
    AnalyzeRequestBuilder ikRequest = new AnalyzeRequestBuilder(elasticsearchTemplate.getClient(),
      AnalyzeAction.INSTANCE, "articlerepo", searchContent);
    ikRequest.setTokenizer("ik_smart");
    List<AnalyzeResponse.AnalyzeToken> ikTokenList = ikRequest.execute().actionGet().getTokens();

    // 循环赋值
    List<String> searchTermList = new ArrayList<>();
    ikTokenList.forEach(ikToken -> {
      searchTermList.add(ikToken.getTerm());
    });

    return searchTermList;
  }

  /**
   * 权重查询
   */
  @Test
  public void scoreQuery() {
    // 分页参数
    Pageable pageable = new PageRequest(0, 5);
    FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
      .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("content", "认同")),
        ScoreFunctionBuilders.weightFactorFunction(1000))
      .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("content", "计划")),
        ScoreFunctionBuilders.weightFactorFunction(100));

    // 创建搜索 DSL (Domain Specific Language特定领域语言) 查询
    SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable).withQuery(functionScoreQueryBuilder).build();
    Page<Article> searchPageResults = articleSearchRepository.search(searchQuery);
    searchPageResults.getContent().forEach(System.out::println);
  }
}




