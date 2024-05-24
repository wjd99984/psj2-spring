package com.prj2spring.mapper.board;

import com.prj2spring.domain.board.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Insert("""
            INSERT INTO board (title, content, member_id)
            VALUES (#{title}, #{content}, #{memberId})
            """)
    public int insert(Board board);

    @Select("""
            SELECT b.id, 
                   b.title,
                   m.nick_name writer
            FROM board b JOIN member m ON b.member_id = m.id
            ORDER BY b.id DESC
            """)
    List<Board> selectAll();

    @Select("""
            SELECT b.id,
                   b.title,
                   b.content,
                   b.inserted,
                   m.nick_name writer,
                    b.member_id
            FROM board b JOIN member m ON b.member_id = m.id
            WHERE b.id = #{id}
            """)
    Board selectById(Integer id);

    @Delete("""
            DELETE FROM board
            WHERE id = #{id}
            """)
    int deleteById(Integer id);

    @Update("""
             UPDATE board
            SET title=#{title},
                content=#{content}
            WHERE id=#{id}
            """)
    int update(Board board);

    @Delete("""
            DELETE FROM board
            WHERE member_id=#{memberId}
            """)
    int deleteByMemberId(Integer memberId);
}
