package gg.loto.raid.web;

import gg.loto.raid.entity.RaidType;
import gg.loto.raid.web.dto.RaidTypeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/raid")
public class RaidApiController {

    @GetMapping
    public ResponseEntity<List<RaidTypeResponse>> getRaidTypes(){
        return ResponseEntity.ok(Arrays.stream(RaidType.values())
                .map(RaidTypeResponse::from)
                .collect(Collectors.toList()));
    }
}
