package com.example.mapper;

import com.example.entity.College;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CollegeMapper {
    void insert(College college);

    @Select("select * from college")
    List<College> selectAll();

    @Select("select * from college where name like concat('%', #{name}, '%')")
    List<College> selectByName(String name);

    void updateById(College college);

    @Delete("delete from college where id = #{id}")
    void deleteById(Integer id);
}
