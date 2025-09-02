package dev.stanislavskyi.feedback_bot_vgr.controller;

import dev.stanislavskyi.feedback_bot_vgr.config.BranchConfig;
import dev.stanislavskyi.feedback_bot_vgr.model.Feedback;
import dev.stanislavskyi.feedback_bot_vgr.model.RoleUser;
import dev.stanislavskyi.feedback_bot_vgr.service.FeedbackStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackStorageService service;
    private final BranchConfig branchProperties;

    @GetMapping
    public String listFilteredFeedbacks(
            @RequestParam(required = false) String autoServiceBranch,
            @RequestParam(required = false) RoleUser roleUser,
            @RequestParam(required = false) Integer criticality,
            Model model) {

        List<Feedback> feedbacks = service.getFilteredFeedbacks(autoServiceBranch, roleUser, criticality);

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("branches", branchProperties.getBranches());
        model.addAttribute("roles", RoleUser.values());
        model.addAttribute("criticalities", List.of(1,2,3,4,5));

        model.addAttribute("selectedRoleUser", roleUser);
        model.addAttribute("selectedBranch", autoServiceBranch);
        model.addAttribute("selectedCriticality", criticality);

        return "feedbacks";
    }


}