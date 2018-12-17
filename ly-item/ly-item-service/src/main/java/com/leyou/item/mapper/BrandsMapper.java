package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface BrandsMapper extends Mapper<Brand> {

    @Insert("insert into tb_category_brand (category_id, brand_id) values(#{cid}, #{bid})")
    void insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Delete("delete tb_category_brand, tb_brand from tb_category_brand, tb_brand where (tb_brand.id = tb_category_brand.brand_id) and tb_brand.id = #{bid}")
    void deleteBrand(Long bid);

    @Select("select tb_b.id,tb_b.name,tb_b.letter,tb_b.image tb_b from tb_brand as tb_b left join tb_category_brand as tb_c_b on tb_b.id=tb_c_b.brand_id where tb_c_b.category_id = #{cid} ")
    List<Brand> queryBrandByCategory(@Param("cid") Long cid);
}
