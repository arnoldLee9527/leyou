package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;


public interface BrandsMapper extends Mapper<Brand> {

    @Insert("insert into tb_category_brand (category_id, brand_id) values(#{cid}, #{bid})")
    void insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Delete("delete tb_category_brand, tb_brand from tb_category_brand, tb_brand where (tb_brand.id = tb_category_brand.brand_id) and tb_brand.id = #{bid}")
    void deleteBrand(Long bid);
}
