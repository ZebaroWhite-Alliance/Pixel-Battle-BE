package ua.cn.stu.pixelbattle.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixelbattle.exception.ApiException;
import ua.cn.stu.pixelbattle.model.Team;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.TeamRepository;

/**
 * Service for managing teams.
 * Handles creation, joining, leaving, and retrieving teams.
 */
@Service
@RequiredArgsConstructor
public class TeamService {

  private final TeamRepository teamRepo;

  /**
   * Creates a new team with the given name and adds the owner to it.
   *
   * @param name  the name of the team
   * @param owner the user creating the team
   * @return the created Team entity
   * @throws IllegalArgumentException if the user is already in a team or team name exists
   */
  @Transactional
  public Team createTeam(String name, User owner) {
    if (getTeamByMember(owner) != null) {
      throw new IllegalArgumentException("You are already in a team");
    }
    if (teamRepo.findByName(name).isPresent()) {
      throw new IllegalArgumentException("Team name already exists");
    }

    Team team = Team.builder()
        .name(name)
        .build();
    team.getMembers().add(owner);
    return teamRepo.save(team);
  }

  /**
   * Adds a user to an existing team by name.
   *
   * @param user     the user joining the team
   * @param teamName the name of the team to join
   * @throws IllegalArgumentException if the team is not found
   * @throws ApiException             if the user is already in another team
   */
  public void joinTeam(User user, String teamName) {
    Team team = teamRepo.findByName(teamName)
        .orElseThrow(() -> new IllegalArgumentException("Team not found"));

    boolean alreadyInTeam = teamRepo.findAll().stream()
        .anyMatch(t -> t.getMembers().contains(user));

    if (alreadyInTeam) {
      throw new ApiException("User already belongs to another team", HttpStatus.BAD_REQUEST);
    }

    team.getMembers().add(user);
    teamRepo.save(team);
  }

  /**
   * Removes the user from their current team.
   * Deletes the team if it becomes empty after the user leaves.
   *
   * @param user the user leaving the team
   * @throws ApiException if the user is not in any team
   */
  public void leaveCurrentTeam(User user) {
    Team currentTeam = teamRepo.findAll().stream()
        .filter(t -> t.getMembers().contains(user))
        .findFirst()
        .orElseThrow(() -> new ApiException("You are not in any team", HttpStatus.BAD_REQUEST));


    currentTeam.getMembers().remove(user);

    if (currentTeam.getMembers().isEmpty()) {
      teamRepo.delete(currentTeam);
    } else {
      teamRepo.save(currentTeam);
    }
  }

  /**
   * Retrieves the team that the given user belongs to.
   *
   * @param user the user whose team is requested
   * @return the Team entity or null if the user is not in any team
   */
  public Team getTeamByMember(User user) {
    return teamRepo.findAll().stream()
        .filter(t -> t.getMembers().contains(user))
        .findFirst()
        .orElse(null);
  }
}
