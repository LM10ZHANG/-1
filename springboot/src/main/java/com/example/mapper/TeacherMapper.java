package com.example.mapper;

import com.example.entity.Teacher;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TeacherMapper {
    void insert(Teacher teacher);

    @Select("select * from teacher where username = #{username}")
    Teacher selectByUsername(String username);

    @Select("select teacher.*, speciality.name as specialityName from teacher left join speciality on teacher.speciality_id = speciality.id")
    List<Teacher> selectAll();

    @Select("select teacher.*, speciality.name as specialityName from teacher " +
            "left join speciality on teacher.speciality_id = speciality.id " +
            "where name like concat('%', #{name}, '%')")
    List<Teacher> selectByName(String name);

    void updateById(Teacher teacher);

    @Delete("delete from teacher where id = #{id}")
    void deleteById(Integer id);
}
