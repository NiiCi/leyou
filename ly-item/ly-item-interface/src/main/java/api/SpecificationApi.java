package api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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


}
