package pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Table(name = "tb_spec_group")
@Data
public class SpecGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cid;
    private String name;
    /**
     * 该规格组下的所有规格参数集合
     */
    @Transient
    private List<SpecParam> params;
}
