package api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pojo.Brand;

import java.util.List;

@RequestMapping("brand")
public interface BrandApi {
    @GetMapping("list")
    public List<Brand>  queryBrandByIds(@RequestParam("ids") List<Long> ids);
}
