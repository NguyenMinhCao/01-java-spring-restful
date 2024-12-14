package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annontaion.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Controller
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;
    private final UserService userService;

    public SubscriberController(SubscriberService subscriberService, UserService userService) {
        this.subscriberService = subscriberService;
        this.userService = userService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("create subscribers")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber)
            throws IdInvalidException {
        boolean exitsEmail = this.userService.isEmailExist(subscriber.getEmail());
        if (exitsEmail == false || subscriber.getEmail() == null || subscriber.getEmail().isEmpty()) {
            throw new IdInvalidException("Email không tồn tại hoặc trống");
        }
        Subscriber res = this.subscriberService.handleCreateSubscriber(subscriber);
        return ResponseEntity.ok().body(res);
    }

    @PutMapping("/subscribers")
    @ApiMessage("update subscribers")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber) throws IdInvalidException {
        Optional<Subscriber> subById = this.subscriberService.findSubscriberById(subscriber.getId());
        if (!subById.isPresent() || subscriber.getId() == null) {
            throw new IdInvalidException("Subscriber không tồn tại hoặc bạn không truyền lên");
        }
        Subscriber res = this.subscriberService.handleUpdateSubscriber(subById.get(), subscriber.getSkills());
        return ResponseEntity.ok().body(res);
    }
}
