package com.example.memo.controller;

import com.example.memo.dto.MemoRequestDto;
import com.example.memo.dto.MemoResponseDto;
import com.example.memo.entity.Memo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/memos") // prefix
public class MemoController {

    // 자료구조가 DB 역할 수행
    private final Map<Long, Memo> memoList = new HashMap<>();

    @PostMapping
    public ResponseEntity<MemoResponseDto> createMemo(@RequestBody MemoRequestDto requestDto) {
        // 식별자가 1씩 증가 하도록 만듦
        Long memoId = memoList.isEmpty() ? 1 : Collections.max(memoList.keySet()) + 1;

        // 요청받은 데이터로 Memo 객체 생성
        Memo memo = new Memo(memoId, requestDto.getTitle(), requestDto.getContents());

        // Inmemory DB에 Memo 저장
        memoList.put(memoId, memo);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.CREATED);
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<MemoResponseDto> findMemoById(@PathVariable Long id) {
        Memo memo = memoList.get(id);

        if(memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MemoResponseDto>> findAllMemos() {
        List<MemoResponseDto> responseList = new ArrayList<>();

        for (Memo memo : memoList.values()) {
            MemoResponseDto responseDto = new MemoResponseDto(memo);
            responseList.add(responseDto);
        }
        return new ResponseEntity<>(responseList, HttpStatus.OK);
        //return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateMemoById(
            @PathVariable Long id,
            @RequestBody MemoRequestDto dto
    ){
        Memo memo = memoList.get(id);

        if(memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(dto.getTitle() == null || dto.getContents() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        memo.update(dto);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateTitle(
            @PathVariable Long id,
            @RequestBody MemoRequestDto dto
    ){
        Memo memo = memoList.get(id);

        if(memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(dto.getTitle() == null || dto.getContents() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        memo.updateTitle(dto);
        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(
            @PathVariable Long id
    ){
        if(memoList.containsKey(id)) {
            memoList.remove(id);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}