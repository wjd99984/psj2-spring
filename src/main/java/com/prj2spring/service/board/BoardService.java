package com.prj2spring.service.board;

import com.prj2spring.domain.board.Board;
import com.prj2spring.domain.board.BoardFile;
import com.prj2spring.mapper.board.BoardMapper;
import com.prj2spring.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper mapper;
    private final MemberMapper memberMapper;

    public void add(Board board, MultipartFile[] files, Authentication authentication) throws IOException {
        board.setMemberId(Integer.valueOf(authentication.getName()));
        // 게시물 저장
        mapper.insert(board);

        if (files != null) {
            for (MultipartFile file : files) {
                // db에 해당 게시물의 파일 목록 저장
                mapper.insertFileName(board.getId(), file.getOriginalFilename());
                // 실제 파일 저장
                // 부모 디렉토리 만들기
                String dir = STR."C:/Temp/prj2/\{board.getId()}";
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }

                // 파일 경로
                String path = STR."C:/Temp/prj2/\{board.getId()}/\{file.getOriginalFilename()}";
                File destination = new File(path);
                file.transferTo(destination);
            }
        }


    }

    public boolean validate(Board board) {
        if (board.getTitle() == null || board.getTitle().isBlank()) {
            return false;
        }

        if (board.getContent() == null || board.getContent().isBlank()) {
            return false;
        }

        return true;
    }

    public Map<String, Object> list(Integer page, String searchType, String keyword) {
        Map pageInfo = new HashMap();
        Integer countAll = mapper.countAllWithSearch(searchType, keyword);

        Integer offset = (page - 1) * 10;
        Integer lastPageNumber = (countAll - 1) / 10 + 1;
        Integer leftPageNumber = (page - 1) / 10 * 10 + 1;
        Integer rightPageNumber = leftPageNumber + 9;
        rightPageNumber = Math.min(rightPageNumber, lastPageNumber);
        leftPageNumber = rightPageNumber - 9;
        leftPageNumber = Math.max(leftPageNumber, 1);
        Integer prevPageNumber = leftPageNumber - 1;
        Integer nextPageNumber = rightPageNumber + 1;

        //  이전,처음,다음,맨끝 버튼 만들기
        if (prevPageNumber > 0) {
            pageInfo.put("prevPageNumber", prevPageNumber);
        }
        if (nextPageNumber <= lastPageNumber) {
            pageInfo.put("nextPageNumber", nextPageNumber);
        }
        pageInfo.put("currentPageNumber", page);
        pageInfo.put("lastPageNumber", lastPageNumber);
        pageInfo.put("leftPageNumber", leftPageNumber);
        pageInfo.put("rightPageNumber", rightPageNumber);

        return Map.of("pageInfo", pageInfo,
                "boardList", mapper.selectAllPaging(offset, searchType, keyword));
    }

    public Board get(Integer id) {
        Board board = mapper.selectById(id);
        List<String> fileNames = mapper.selectFileNameByBoardId(id);
        // http://172.30.1.57:8888/{id}/{name}
        List<BoardFile> files = fileNames.stream()
                .map(name -> new BoardFile(name, STR."http://172.30.1.57:8888/\{id}/\{name}"))
                .toList();

        board.setFiles(files);

        return board;
    }

    public void remove(Integer id) {
        // file 명 조회
        List<String> fileNames = mapper.selectFileNameByBoardId(id);

        // disk 에 있는 file
        String dir = STR."C:/Temp/prj2/\{id}/";
        for (String fileName : fileNames) {
            File file = new File(dir + fileName);
            file.delete();
        }
        File dirFile = new File(dir);
        if (dirFile.exists()) {
            dirFile.delete();
        }

        // board_file
        mapper.deleteFileByBoardId(id);

        // board
        mapper.deleteById(id);
    }

    public void edit(Board board) {
        mapper.update(board);
    }

    public boolean hasAccess(Integer id, Authentication authentication) {
        Board board = mapper.selectById(id);

        return board.getMemberId()
                .equals(Integer.valueOf(authentication.getName()));
    }
}
