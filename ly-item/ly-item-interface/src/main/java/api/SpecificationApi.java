package api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pojo.SpecGroup;
import pojo.SpecParam;

import java.util.List;

@RequestMapping("spec")
public interface SpecificationApi {
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid") Long cid );

    @GetMapping("/params")
    public List<SpecParam> querySpecParams(
            @RequestParam(value="gid",required = false) Long gid,
            @RequestParam(value="cid",required = false) Long cid,
            @RequestParam(value="searching",required = false) Boolean searching,
            @RequestParam(value="generic",required = false) Boolean generic);

    /**
     * 查询规格参数组，及组内参数
     * @param cid
     * @return
     */
    @GetMapping("{cid}")
    List<SpecGroup> querySpecsByCid(@PathVariable("cid") Long cid);
}
