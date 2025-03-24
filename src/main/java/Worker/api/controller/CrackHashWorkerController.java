package Worker.api.controller;

import Worker.business_logic.services.CrackHashWorkerService;
import Worker.api.dto.CrackTaskRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.env.Environment;

@RestController
@RequestMapping("/internal/api/worker/hash/crack")
public class CrackHashWorkerController {

    private final CrackHashWorkerService crackHashWorkerService;
    private final Environment environment;

    public CrackHashWorkerController(CrackHashWorkerService crackHashWorkerService,Environment environment) {
        this.crackHashWorkerService = crackHashWorkerService;
        this.environment = environment;

        String port = environment.getProperty("server.port");
        System.out.println("my port "+port);
    }

    @PostMapping("/task")
    public ResponseEntity<Void> receiveTask(@RequestBody CrackTaskRequestDTO taskRequest)
    {
        System.out.println("start uuid = "+taskRequest.getRequestId()+", hash = "+taskRequest.getHash());
        crackHashWorkerService.addToQueue(taskRequest);
        System.out.println("the end uuid = "+taskRequest.getRequestId()+", hash = "+taskRequest.getHash());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(@RequestParam("id") String requestId)
    {
        return ResponseEntity.ok(crackHashWorkerService.getStatus(requestId));
    }
}