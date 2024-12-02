package com.gym.crm.service.impl;

import com.gym.crm.dto.TraineeProfile;
import com.gym.crm.dto.UpdateTraineeRequest;
import com.gym.crm.dto.UserDetailsResponse;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ServiceException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.User;
import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.repository.UserRepository;
import com.gym.crm.security.CustomPasswordEncoder;
import com.gym.crm.service.TraineeService;
import com.gym.crm.specification.SearchCriteria;
import com.gym.crm.specification.SpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository repository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final ProfileService profileService;
    private final CustomPasswordEncoder passwordEncoder;
    private final AnalyticsSender sender;

    @Autowired
    public TraineeServiceImpl(TraineeRepository traineeRepository, TrainerRepository trainerRepository, ProfileService profileService,
                              UserRepository userRepository, TrainingRepository trainingRepository, CustomPasswordEncoder passwordEncoder, AnalyticsSender sender) {
        this.repository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.profileService = profileService;
        this.passwordEncoder = passwordEncoder;
        this.sender = sender;
    }

    @Override
    @Transactional
    public UserDetailsResponse create(TraineeProfile traineeProfile) {
        if (traineeProfile == null) {
            throw new ServiceException("Trainee cannot be null");
        }

        String username = traineeProfile.getFirstName() + "." + traineeProfile.getLastName();
        List<String> duplicateNames = userRepository.findByUsernamesStartedFrom(username);

        String newPassword = profileService.createNewPassword();

        User user = User.builder()
                .firstName(traineeProfile.getFirstName())
                .lastName(traineeProfile.getLastName())
                .username(profileService.buildUsername(username, duplicateNames))
                .isActive(true)
                .password(passwordEncoder.generatePasswordHash(newPassword))
                .build();

        Trainee trainee = Trainee.builder()
                .dateOfBirth(traineeProfile.getDateOfBirth())
                .address(traineeProfile.getAddress())
                .user(user).build();

        repository.save(trainee);

        return new UserDetailsResponse().username(user.getUsername()).password(newPassword);
    }

    @Override
    @Transactional
    public Trainee update(UpdateTraineeRequest request) {
        if (request == null) {
            throw new ServiceException("Trainee cannot be null");
        }

        Trainee traineeFromStorage = repository.findByUserUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found in database"));

        User user = User.builder()
                .id(traineeFromStorage.getUser().getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .isActive(request.getIsActive())
                .password(traineeFromStorage.getUser().getPassword())
                .build();

        Trainee trainee = Trainee.builder()
                .id(traineeFromStorage.getId())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .trainers(traineeFromStorage.getTrainers())
                .user(user)
                .build();

        return repository.save(trainee);
    }

    @Override
    public boolean matchUsernameAndPassword(String username, String password) {
        if (username == null || password == null) {
            throw new ServiceException("Trainee username or password cannot be null");
        }

        if (password.length() != 10) {
            throw new ServiceException("password length must be 10");
        }

        String passwordFromDatabase = userRepository.retrievePasswordByUsername(username);

        return passwordFromDatabase != null && passwordFromDatabase.equals(passwordEncoder.generatePasswordHash(password));
    }

    @Override
    @Transactional
    public boolean changePassword(String username, String password) {
        if (username == null || password == null) {
            throw new ServiceException("Trainee username or password cannot be null");
        }

        if (password.length() != 10) {
            throw new ServiceException("password length must be 10");
        }

        userRepository.changePassword(username, passwordEncoder.generatePasswordHash(password));

        return true;
    }

    @Override
    @Transactional
    public boolean changeIsActive(String username, boolean isActive) {
        if (username == null) {
            throw new ServiceException("Trainee username cannot be null");
        }

        userRepository.changeIsActive(username, isActive);
        return true;
    }

    @Override
    public List<Training> getTrainingsByCriteria(List<SearchCriteria> critters) {
        SpecificationsBuilder builder = new SpecificationsBuilder(critters);
        Specification<Training> spec = builder.build();

        return trainingRepository.findAll(spec);
    }

    @Override
    @Transactional
    public boolean delete(String username) {
        if (username == null) {
            throw new ServiceException("Trainee username cannot be null");
        }

        Trainee trainee = repository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(format("Trainee for deleting: %s not found in Repository", username)));

        sender.processWorkload(trainingRepository.findByTraineeUserUsername(username), "DELETE");
        repository.delete(trainee);

        return true;
    }

    @Override
    public Trainee findByUsername(String username) {
        if (username == null) {
            throw new ServiceException("Username trainee cannot be null");
        }

        return repository.findByUserUsername(username).orElse(null);
    }

    public List<Trainer> getTrainersNotAssignedForTrainee(String username) {
        if (username == null) {
            throw new ServiceException("Username trainee cannot be null");
        }

        return repository.getTrainersNotAssignedForTrainee(username);
    }

    @Transactional
    public Trainee updateTrainersForTrainee(String traineeUsername, List<String> trainerUsernames) {
        if (trainerUsernames == null) {
            throw new ServiceException("Trainers for updating was null");
        }

        Trainee trainee = repository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException(format("Trainee: %s not found in Repository", traineeUsername)));

        trainee.getTrainers().size();

        List<Trainer> newTrainers = trainerRepository.findByUserUsernameIn(trainerUsernames);

        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(newTrainers);

        return trainee;
    }

    @Override
    public Set<Trainer> getTrainersTrainee(String username) {
        if (username == null) {
            throw new ServiceException("Username trainee cannot be null");
        }

        Trainee trainee = repository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(format("Trainee: %s not found in Repository", username)));

        return trainee.getTrainers();
    }
}
