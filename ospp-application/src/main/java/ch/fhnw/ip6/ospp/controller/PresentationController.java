package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.mapper.PresentationMapper;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.service.PresentationService;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import ch.fhnw.ip6.ospp.vo.RoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/presentation")
@RequiredArgsConstructor
public class PresentationController {

    private final PresentationService presentationService;
    private final PresentationMapper presentationMapper;

    @GetMapping
    public ResponseEntity<List<PresentationVO>> findAll() {
        return ResponseEntity
                .ok()
                .body(
                        presentationService.getAll()
                                .stream().map(presentationMapper::fromEntityToVo).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresentationVO> byId(@PathVariable Long id) {
        return presentationService.findById(id)
                .map(presentaion ->
                        ResponseEntity.ok().body(presentationMapper.fromEntityToVo(presentaion)))
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @PostMapping
    public ResponseEntity<PresentationVO> save(@RequestBody Presentation presentation) {
        return ResponseEntity
                .ok()
                .body(presentationMapper.fromEntityToVo(presentationService.save(presentation)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        presentationService.delete(id);
    }

}
