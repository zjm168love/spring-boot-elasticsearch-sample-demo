package springbootelastic.elasticrepo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import springbootelastic.domin.Article;

import java.util.List;

/**
 * @author FCB
 * <p>
 * 泛型的参数分别是实体类型和主键类型
 */
public interface ArticleSearchRepository extends ElasticsearchRepository<Article, Long> {
  List<Article> findById(Long id);

  Page<Article> findByTitle(String title, Pageable pageable);

}