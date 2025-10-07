package ua.cn.stu.pixelbattle.controller;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.cn.stu.pixelbattle.dto.TeamActionRequest;
import ua.cn.stu.pixelbattle.dto.TeamInfoResponse;
import ua.cn.stu.pixelbattle.model.Team;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;
import ua.cn.stu.pixelbattle.service.TeamService;
import ua.cn.stu.pixelbattle.service.UserService;

/**
 * REST controller for managing teams.
 * Supports creating teams, joining/leaving, and retrieving team info.
 */
@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {


  private final TeamService teamService;
  private final UserService userService;

  /**
   * Creates a new team and adds the current user to it.
   *
   * @param request DTO with team information
   * @param userDetails current user
   * @return information about the created team
   */
  @PostMapping("/create")
  public ResponseEntity<?> createTeam(@RequestBody TeamInfoResponse request,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
    User user = userService.getUserByUsername(userDetails.getUsername())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    Team team = teamService.createTeam(request.getName(), user);
    return ResponseEntity.ok(toDto(team));
  }

  /**
   * Adds the current user to an existing team.
   *
   * @param request DTO with the team name
   * @param userDetails current user
   * @return message confirming successful join
   */
  @PostMapping("/join")
  public ResponseEntity<?> joinTeam(@RequestBody TeamActionRequest request,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
    User user = userService.getUserByUsername(userDetails.getUsername())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    teamService.joinTeam(user, request.getName());
    return ResponseEntity.ok(Map.of(
        "message", "Joined team",
        "team", request.getName()
    ));
  }

  /**
   * Removes the current user from their team.
   *
   * @param userDetails current user
   * @return message confirming successful leave
   */
  @PostMapping("/leave")
  public ResponseEntity<?> leaveTeam(@AuthenticationPrincipal CustomUserDetails userDetails) {
    User user = userService.getUserByUsername(userDetails.getUsername())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    teamService.leaveCurrentTeam(user);
    return ResponseEntity.ok(Map.of("message", "You left your team"));
  }

  /**
   * Retrieves information about the team the current user belongs to.
   *
   * @param userDetails current user
   * @return team info or message if the user is not in a team
   */
  @GetMapping("/info")
  public ResponseEntity<?> getMyTeamInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
    User user = userService.getUserByUsername(userDetails.getUsername())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Team team = teamService.getTeamByMember(user);
    if (team == null) {
      return ResponseEntity.ok(Map.of("message", "You are not in a team"));
    }

    return ResponseEntity.ok(toDto(team));
  }

  /**
   * Converts a Team entity to a DTO for client responses.
   *
   * @param team team entity
   * @return DTO with team information
   */
  private TeamInfoResponse toDto(Team team) {
    List<String> members = team.getMembers()
        .stream()
        .map(User::getUsername)
        .collect(Collectors.toList());
    return new TeamInfoResponse(team.getName(), team.getCreatedAt(), members);
  }
}
