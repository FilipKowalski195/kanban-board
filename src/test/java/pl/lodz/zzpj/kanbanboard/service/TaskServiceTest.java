package pl.lodz.zzpj.kanbanboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lodz.zzpj.kanbanboard.entity.Project;
import pl.lodz.zzpj.kanbanboard.entity.Review;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails.Difficulty;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.exceptions.BaseException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.repository.ProjectsRepository;
import pl.lodz.zzpj.kanbanboard.repository.TasksRepository;
import pl.lodz.zzpj.kanbanboard.repository.UsersRepository;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    ProjectsRepository projectsRepository;

    @Mock
    TasksRepository tasksRepository;

    @Mock
    UsersRepository usersRepository;

    @Mock
    DateProvider dateProvider;

    TaskService taskService;

    final String defaultEmail = "user@email.com";
    final String leaderEmail = "leader@email.com";

    final User defaultLeader = new User(UUID.randomUUID(), Instant.now(), leaderEmail, "Damian", "Baczyński", "P@ssw0rd1");
    final User defaultUser = new User(UUID.randomUUID(), Instant.now(), defaultEmail, "Filip", "Kowalski", "P@ssw0rd2");

    final UUID taskUUID = UUID.randomUUID();
    final TaskDetails defaultTaskDetails = new TaskDetails("Default Task", "It is just a default task", Instant.MAX, Difficulty.MEDIUM);
    final Task defaultTask = new Task(taskUUID, Instant.now(), defaultLeader, defaultTaskDetails);

    final Review negativeReview = new Review(UUID.randomUUID(), Instant.now(), defaultLeader, "Very Bad", true);
    final Review positiveReview = new Review(UUID.randomUUID(), Instant.now(), defaultLeader, "Very Good", false);

    final UUID projectUuid = UUID.randomUUID();
    final Project defaultProject = new Project(projectUuid, "Default Project", Instant.now(), defaultLeader);

    private void noMoreInteractions() {
        verifyNoMoreInteractions(tasksRepository, usersRepository, projectsRepository, dateProvider);
    }

    private void prepareTaskService() {
        taskService = new TaskService(tasksRepository, projectsRepository, usersRepository, dateProvider);
    }

    @Test
    void getAllTaskAssignedTo_TasksExtracted() {
        prepareTaskService();

        taskService.getAllTaskAssignedTo(defaultUser.getEmail());

        verify(tasksRepository).findAllByAssignee_Email(defaultUser.getEmail());

        noMoreInteractions();
    }

    @Test
    void getAllTasksCreatedBy_TasksExtracted() {
        prepareTaskService();

        taskService.getAllTasksCreatedBy(defaultLeader.getEmail());

        verify(tasksRepository).findAllByCreator_Email(defaultLeader.getEmail());

        noMoreInteractions();
    }

    @Test
    void getTaskByUUID_taskExist_TaskExtracted() throws NotFoundException {

        when(tasksRepository.findByUuid(taskUUID))
                .thenReturn(Optional.of(defaultTask));

        prepareTaskService();

        taskService.getTaskByUUID(taskUUID);

        verify(tasksRepository).findByUuid(taskUUID);

        noMoreInteractions();
    }

    @Test
    void updateTaskDetails_taskExist_TaskUpdated() throws BaseException {

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        prepareTaskService();

        String newDescription = "Just updating";

        taskService.updateTaskDetails(taskUUID, "Default Task", newDescription, Instant.MAX, Difficulty.MEDIUM);

        assertThat(defaultTask.getDetails().getDescription()).isEqualTo(newDescription);

        verify(tasksRepository).findByUuid(taskUUID);
    }

    @Test
    void updateTaskDetails_taskDoesNotExist_ExceptionThrown() {

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.empty());
        ;

        prepareTaskService();

        String newDescription = "Just updating";

        assertThatThrownBy(() -> taskService.updateTaskDetails(taskUUID, "Default Task", newDescription, Instant.MAX, Difficulty.MEDIUM))
                .isInstanceOf(NotFoundException.class);

        verify(tasksRepository).findByUuid(taskUUID);

        noMoreInteractions();
    }

    @Test
    void changeStatus_taskIsAssignedAndCompleted_StatusChanged() throws BaseException {

        defaultTask.setAssignee(defaultUser);

        defaultTask.setStatus(Status.TO_REVIEW);

        defaultTask.getDetails().getReviews().add(positiveReview);

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        prepareTaskService();

        taskService.changeStatus(taskUUID, Status.DONE);

        verify(tasksRepository).findByUuid(taskUUID);

        assertThat(taskService.getTaskByUUID(taskUUID).getStatus()).isEqualTo(Status.DONE);

    }

    @Test
    void changeStatus_taskIsNotReviewed_ExceptionThrown() throws NotFoundException {

        defaultTask.setAssignee(defaultUser);

        defaultTask.setStatus(Status.TO_REVIEW);

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        prepareTaskService();

        assertThatThrownBy(() -> taskService.changeStatus(taskUUID, Status.DONE)).isInstanceOf(
                ConflictException.class
        );

        verify(tasksRepository).findByUuid(taskUUID);

        assertThat(taskService.getTaskByUUID(taskUUID).getStatus()).isEqualTo(Status.TO_REVIEW);

        noMoreInteractions();
    }

    @Test
    void changeStatus_taskIsRejected_ExceptionThrown() throws BaseException {

        defaultTask.setAssignee(defaultUser);

        defaultTask.setStatus(Status.TO_REVIEW);

        defaultTask.getDetails().getReviews().add(negativeReview);

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        prepareTaskService();

        assertThatThrownBy(() -> taskService.changeStatus(taskUUID, Status.DONE)).isInstanceOf(
                ConflictException.class
        );

        verify(tasksRepository).findByUuid(taskUUID);

        assertThat(taskService.getTaskByUUID(taskUUID).getStatus()).isEqualTo(Status.TO_REVIEW);

        noMoreInteractions();
    }

    @Test
    void changeStatus_taskIsNotAssigned_ExceptionThrown() throws BaseException {

        defaultTask.setStatus(Status.TODO);

        defaultTask.getDetails().getReviews().add(positiveReview);

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        prepareTaskService();

        assertThatThrownBy(() -> taskService.changeStatus(taskUUID, Status.IN_PROGRESS)).isInstanceOf(
                ConflictException.class
        );

        verify(tasksRepository).findByUuid(taskUUID);

        assertThat(taskService.getTaskByUUID(taskUUID).getStatus()).isEqualTo(Status.TODO);
    }

    @Test
    void changeStatus_taskIsNotAssignedOrReviewed_StatusChangedToCanceled() throws BaseException {

        defaultTask.setStatus(Status.TODO);

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        prepareTaskService();

        taskService.changeStatus(taskUUID, Status.CANCELED);

        verify(tasksRepository).findByUuid(taskUUID);

        assertThat(taskService.getTaskByUUID(taskUUID).getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    void assign_UserExistIsInProjectTaskExistAndIsNotFinished_TaskAssigned() throws BaseException {

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        when((usersRepository.findUserByEmail(defaultEmail))).thenReturn(Optional.of(defaultUser));

        when((projectsRepository.findProjectByTasksContains(defaultTask))).thenReturn(Optional.of(defaultProject));

        when((projectsRepository.findProjectByUuidAndMembersContains(defaultProject.getUuid(), defaultUser)))
                .thenReturn(Optional.of(defaultProject));

        prepareTaskService();

        taskService.assign(taskUUID, defaultEmail);

        verify(tasksRepository).findByUuid(taskUUID);

        assertThat(taskService.getTaskByUUID(taskUUID).getAssignee()).isEqualTo(defaultUser);
    }

    @Test
    void assign_UserDoesNotExistIsInProjectTaskExistAndIsNotFinished_ExceptionThrown() throws BaseException {

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        when((usersRepository.findUserByEmail(defaultEmail))).thenReturn(Optional.of(defaultUser));

        when((projectsRepository.findProjectByTasksContains(defaultTask))).thenReturn(Optional.of(defaultProject));

        when((projectsRepository.findProjectByUuidAndMembersContains(defaultProject.getUuid(), defaultUser)))
                .thenReturn(Optional.empty());

        prepareTaskService();

        assertThatThrownBy(() -> taskService.assign(taskUUID, defaultEmail)).isInstanceOf(
                ConflictException.class
        );

        verify(tasksRepository).findByUuid(taskUUID);

        assertThat(taskService.getTaskByUUID(taskUUID).getAssignee()).isEqualTo(null);
    }

    @Test
    void assign_UserExistIsInProjectTaskDoesNotExist_ExceptionThrown() {

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.empty());

        prepareTaskService();

        assertThatThrownBy(() -> taskService.assign(taskUUID, defaultEmail)).isInstanceOf(
                NotFoundException.class
        );

        assertThat(taskService.getAllTaskAssignedTo(defaultEmail)).isEmpty();
    }

    @Test
    void assign_UserExistIsInProjectTaskExistAndIsFinished_ExceptionThrown() throws BaseException {
        defaultTask.setStatus(Status.DONE);

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        prepareTaskService();

        assertThatThrownBy(() -> taskService.assign(taskUUID, defaultEmail)).isInstanceOf(
                ConflictException.class
        );

        verify(tasksRepository).findByUuid(taskUUID);

        assertThat(taskService.getAllTaskAssignedTo(defaultEmail)).isEmpty();
    }

    @Test
    void reviewTask_TaskAndReviewerExistsInProject_TaskReviewed() throws BaseException {

        defaultTask.setStatus(Status.TO_REVIEW);

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        when((usersRepository.findUserByEmail(leaderEmail))).thenReturn(Optional.of(defaultLeader));

        when((projectsRepository.findProjectByTasksContains(defaultTask))).thenReturn(Optional.of(defaultProject));

        when((projectsRepository.findProjectByUuidAndMembersContains(defaultProject.getUuid(), defaultLeader)))
                .thenReturn(Optional.of(defaultProject));

        prepareTaskService();

        taskService.reviewTask(taskUUID, leaderEmail, "Just a comment", false);

        verify(tasksRepository).findByUuid(taskUUID);

        assertThat(taskService.getTaskByUUID(taskUUID).getDetails().getReviews()).isNotEmpty();
    }

    @Test
    void reviewTask_TaskAndReviewerExistsInProjectTaskStatusNotToReview_ExceptionThrown() throws BaseException {

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        prepareTaskService();

        assertThatThrownBy(() -> taskService.reviewTask(taskUUID, leaderEmail, "Just a comment", false)).isInstanceOf(
                ConflictException.class
        );

        verify(tasksRepository).findByUuid(taskUUID);

        assertThat(taskService.getTaskByUUID(taskUUID).getDetails().getReviews()).isEmpty();
    }

    @Test
    void reviewTask_TaskDoesntExist_ExceptionThrown()  {

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.empty());

        prepareTaskService();

        assertThatThrownBy(() -> taskService.reviewTask(taskUUID, leaderEmail, "Just a comment", false)).isInstanceOf(
                BaseException.class
        );
    }

    @Test
    void reviewTask_ReviewerDoesntExist_TaskReviewed() {

        defaultTask.setStatus(Status.TO_REVIEW);

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        when((usersRepository.findUserByEmail(leaderEmail))).thenReturn(Optional.empty());

        prepareTaskService();

        assertThatThrownBy(() -> taskService.reviewTask(taskUUID, leaderEmail, "Just a comment", false)).isInstanceOf(
                BaseException.class
        );
    }

    @Test
    void reviewTask_TaskAndReviewerExistsNotInProject_TaskReviewed() throws BaseException {

        defaultTask.setStatus(Status.TO_REVIEW);

        when((tasksRepository.findByUuid(taskUUID))).thenReturn(Optional.of(defaultTask));

        when((usersRepository.findUserByEmail(leaderEmail))).thenReturn(Optional.of(defaultLeader));

        when((projectsRepository.findProjectByTasksContains(defaultTask))).thenReturn(Optional.of(defaultProject));

        when((projectsRepository.findProjectByUuidAndMembersContains(defaultProject.getUuid(), defaultLeader)))
                .thenReturn(Optional.empty());

        prepareTaskService();

        assertThatThrownBy(() -> taskService.reviewTask(taskUUID, leaderEmail, "Just a comment", false)).isInstanceOf(
                BaseException.class
        );

        assertThat(taskService.getTaskByUUID(taskUUID).getDetails().getReviews()).isEmpty();
    }
}
