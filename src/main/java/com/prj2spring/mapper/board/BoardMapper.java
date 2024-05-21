package com.prj2spring.mapper.board;

import com.prj2spring.domain.board.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Insert("""
            INSERT INTO board (title, content, writer)
            VALUES (#{title}, #{content}, #{writer})
            """)
    public int insert(Board board);

    @Select("""
            SELECT id, title, writer, inserted
            FROM board
            ORDER BY id DESC
            """)
    List<Board> selectAll();

    @Select("""
            SELECT *
            FROM board
            WHERE id = #{id}
            """)
    Board selectById(Integer id);

    @Delete("""
                DELETE FROM board
                WHERE id = #{id}
            """)
    int deleteById(Integer id);

    @Update("""
                UPDATE  board
                SET title=#{title}, content=#{content}, writer=#{writer}
                WHERE id=#{id}
            """)
    int update(Board board);
}
