package com.stagelog.Stagelog.api.admin;

import com.stagelog.Stagelog.service.ArtistBootStrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final ArtistBootStrapService service;

    @PostMapping("/api/admin/bootstrap")
    public ArtistBootStrapService.BootstrapResult bootstrap() {
        return service.execute();
    }
}
