package springbootelastic.domin;

import lombok.Data;
@Data
public class Author{
  /**
   * 作者id
   */
  private Long id;
  /**
   * 作者姓名
   */
  private String name;
  /**
   * 作者简介
   */
  private String remark;

  //setters and getters
  //toString

}