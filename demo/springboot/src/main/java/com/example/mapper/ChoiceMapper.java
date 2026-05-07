package com.example.mapper;

import com.example.entity.Choice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ChoiceMapper {
    void insert(Choice choice);

    @Select("select choice.*, teacher.name as teacherName, student.name as studentName from choice " +
            "left join teacher on choice.teacher_id = teacher.id " +
            "left join student on choice.student_id = student.id "
    )
    List<Choice> selectAll();

    @Select("select choice.*, teacher.name as teacherName, student.name as studentName from choice " +
            "left join teacher on choice.teacher_id = teacher.id " +
            "left join student on choice.student_id = student.id " +
            "where choice.name like concat('%', #{name}, '%')")
    List<Choice> selectByName(String name);

    void updateById(Choice choice);

    @Delete("delete from choice where id = #{id}")
    void deleteById(Integer id);

    @Select("select * from choice where course_id = #{courseId} and student_id = #{studentId}")
    List<Choice> selectByCourseIdAndStudentId(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);

    @Select("select choice.*, teacher.name as teacherName, student.name as studentName from choice " +
            "left join teacher on choice.teacher_id = teacher.id " +
            "left join student on choice.student_id = student.id " +
            "where choice.name like concat('%', #{name}, '%') and student_id = #{studentId}")
    List<Choice> selectByNameAndStudentId(@Param("name") String name, @Param("studentId") Integer studentId);

    @Select("select choice.*, teacher.name as teacherName, student.name as studentName from choice " +
            "left join teacher on choice.teacher_id = teacher.id " +
            "left join student on choice.student_id = student.id " +
            "where student_id = #{studentId}"
    )
    List<Choice> selectAllByStudentId(Integer studentId);

    @Select("select choice.*, teacher.name as teacherName, student.name as studentName from choice " +
            "left join teacher on choice.teacher_id = teacher.id " +
            "left join student on choice.student_id = student.id " +
            "where choice.name like concat('%', #{name}, '%') and teacher_id = #{teacherId}")
    List<Choice> selectByNameAndTeacherId(@Param("name") String name, @Param("teacherId") Integer teacherId);

    @Select("select choice.*, teacher.name as teacherName, student.name as studentName from choice " +
            "left join teacher on choice.teacher_id = teacher.id " +
            "left join student on choice.student_id = student.id " +
            "where teacher_id = #{teacherId}"
    )
    List<Choice> selectAllByTeacherId(Integer teacherId);

    @Select("select * from choice where id = #{id}")
    Choice selectById(Integer id);
}
