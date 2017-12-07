package springbootelastic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import springbootelastic.domin.Article;
import springbootelastic.domin.Author;
import springbootelastic.elasticrepo.ArticleSearchRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@SpringBootApplication
public class ElasticApplication implements CommandLineRunner {
  @Autowired
  private ArticleSearchRepository articleSearchRepository;

  @Autowired
  private ElasticsearchTemplate elasticsearchTemplate;

  public static void main(String[] args) {
    SpringApplication.run(ElasticApplication.class, args);
  }

  ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void run(String... arg0) throws Exception {
    elasticsearchTemplate.deleteIndex(Article.class);
    elasticsearchTemplate.createIndex(Article.class);
    elasticsearchTemplate.putMapping(Article.class);
    elasticsearchTemplate.refresh(Article.class);


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


   /* //查询所有记录
    Iterable<Article> findAll = articleSearchRepository.findAll();
    findAll.forEach(System.out::println);

    //修改
    Long articleId = 1L;
    List<Article> updateSources = articleSearchRepository.findById(articleId);
    Article article = updateSources.get(0);
    article.setContent("update now");
    articleSearchRepository.save(article);

    //根据id查询
    List<Article> findById = articleSearchRepository.findById(1L);
    System.out.println("findById   ----------- size : " + findById.size());
    findById.forEach(System.out::println);

    //分页查询
    Page<Article> articlePage = articleSearchRepository.findByAuthor("狂扁小朋友", new PageRequest(0, 2));
    System.out.println("分页查询:");
    articlePage.forEach(System.out::println);


 *//*   //关键字查询
    String queryString = "水电费";
    QueryStringQueryBuilder builder = new QueryStringQueryBuilder(queryString);
//    builder.
    Iterable<Article> searchResult = articleSearchRepository.search(builder);
    System.out.println("遍历结果------------------------------");
    Iterator<Article> iterator = searchResult.iterator();
    while (iterator.hasNext()) {
      System.out.println(iterator.next());
    }*//*
   *//* //联合查询
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    boolQuery.must(QueryBuilders.termQuery("abstracts", "elasticsearch"));
    boolQuery.must(QueryBuilders.termQuery("content", "you"));
    Iterable<Article> jointResult = articleSearchRepository.search(boolQuery);
    Iterator<Article> iteratorJoint = jointResult.iterator();
    System.out.println("联合查询:");
    while (iteratorJoint.hasNext()) {
      System.out.println(iteratorJoint.next());
    }*/

/*    //联合查询
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

    //boolQuery.must(QueryBuilders.simpleQueryStringQuery("水电费"));
   // boolQuery.must(QueryBuilders.termQuery("abstracts", "elasticsearch"));
    boolQuery.must(QueryBuilders.termQuery("abstracts", "水电费"));
    Iterable<Article> jointResult = articleSearchRepository.search(boolQuery);
    Iterator<Article> iteratorJoint = jointResult.iterator();
    System.out.println("联合查询:");
    while (iteratorJoint.hasNext()) {
      System.out.println(iteratorJoint.next());
    }*/
  }
}
