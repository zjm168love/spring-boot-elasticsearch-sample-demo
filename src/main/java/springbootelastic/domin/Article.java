package springbootelastic.domin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author FCB
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "articlerepo", type = "article", shards = 1, replicas = 0)
public class Article {
  @Id
  private Long id;

  @Field(type = FieldType.Integer)
  private Integer articleId;
  /**
   * 标题
   */
  private String title;
  /**
   * 摘要
   */
  @Field(type=FieldType.String,index = FieldIndex.not_analyzed)
  private String abstracts;
  /**
   * 内容
   */
  @Field(type = FieldType.String,searchAnalyzer = "ik_smart", analyzer = "ik_smart")
  private String content;
  /**
   * 发表时间
   */
  private Date postTime;
  /**
   * 点击率
   */
  private Long clickCount;
  /**
   * 作者
   */
  @Field(type = FieldType.Nested)
  private Author author;


}