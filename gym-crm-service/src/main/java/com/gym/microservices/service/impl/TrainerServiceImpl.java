package com.gym.microservices.service.impl;

import com.gym.microservices.dto.TrainerProfile;
import com.gym.microservices.dto.UpdateTrainerRequest;
import com.gym.microservices.dto.UserDetailsResponse;
import com.gym.microservices.exception.EntityNotFoundException;
import com.gym.microservices.exception.ServiceException;
import com.gym.microservices.model.Trainer;
import com.gym.microservices.model.Training;
import com.gym.microservices.model.TrainingType;
import com.gym.microservices.model.User;
import com.gym.microservices.repository.TrainerRepository;
import com.gym.microservices.repository.TrainingRepository;
import com.gym.microservices.repository.TrainingTypeRepository;
import com.gym.microservices.repository.UserRepository;
import com.gym.microservices.security.CustomPasswordEncoder;
import com.gym.microservices.service.TrainerService;
import com.gym.microservices.specification.SearchCriteria;
import com.gym.microservices.specification.SpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository repository;
    private final UserRepository userRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingRepository trainingRepository;
    private final ProfileService profileService;
    private final CustomPasswordEncoder passwordEncoder;

    @Autowired
    public TrainerServiceImpl(TrainerRepository trainerRepository,
                              UserRepository userRepository,
                              TrainingTypeRepository trainingTypeRepository, TrainingRepository trainingRepository,
                              ProfileService profileService, CustomPasswordEncoder passwordEncoder) {
        this.repository = trainerRepository;
        this.userRepository = userRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingRepository = trainingRepository;
        this.profileService = profileService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDetailsResponse create(TrainerProfile trainerProfile) {
        if (trainerProfile == null) {
            throw new ServiceException("Trainer cannot be null");
        }

        TrainingType trainingType = trainingTypeRepository.findByName(trainerProfile.getSpecialization().getValue())
                .orElseThrow(() -> new ServiceException("Specialization for trainer cannot be null"));

        String username = trainerProfile.getFirstName() + "." + trainerProfile.getLastName();

        List<String> duplicateNames = userRepository.findByUsernamesStartedFrom(username);

        String newPassword = profileService.createNewPassword();
        User user = User.builder()
                .firstName(trainerProfile.getFirstName())
                .lastName(trainerProfile.getLastName())
                .username(profileService.buildUsername(username, duplicateNames))
                .isActive(true)
                .password(passwordEncoder.generatePasswordHash(newPassword))
                .build();

        Trainer trainer = Trainer.builder()
                .specialization(trainingType)
                .user(user)
                .build();

        repository.save(trainer);

        return new UserDetailsResponse().username(user.getUsername()).password(newPassword);
    }

    @Override
    @Transactional
    public Trainer update(UpdateTrainerRequest trainerRequest) {
        if (trainerRequest == null) {
            throw new ServiceException("Trainer cannot be null");
        }

        Trainer trainerFromStorage = repository.findByUserUsername(trainerRequest.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found in database"));

        User user = User.builder()
                .id(trainerFromStorage.getUser().getId())
                .firstName(trainerRequest.getFirstName())
                .lastName(trainerRequest.getLastName())
                .username(trainerRequest.getUsername())
                .isActive(trainerRequest.getIsActive())
                .password(trainerFromStorage.getUser().getPassword())
                .build();

        TrainingType trainingType = trainingTypeRepository.findByName(trainerRequest.getSpecialization().name())
                .orElseThrow(() -> new ServiceException("Specialization for trainer cannot be null"));

        Trainer trainer = Trainer.builder().id(trainerFromStorage.getId()).specialization(trainingType).trainees(trainerFromStorage.getTrainees()).user(user).build();

        return repository.save(trainer);
    }

    @Override
    public Trainer findByUsername(String username) {
        if (username == null) {
            throw new ServiceException("Username trainer cannot be null");
        }

        return repository.findByUserUsername(username).orElse(null);
    }

    @Override
    public boolean matchUsernameAndPassword(String username, String password) {
        if (username == null || password == null) {
            throw new ServiceException("Trainer username or password cannot be null");
        }

        String passwordFromDatabase = userRepository.retrievePasswordByUsername(username);

        return passwordFromDatabase != null && passwordFromDatabase.equals(passwordEncoder.generatePasswordHash(password));
    }

    @Override
    @Transactional
    public boolean changePassword(String username, String password) {
        if (username == null || password == null) {
            throw new ServiceException("Trainer username or password cannot be null");
        }

        userRepository.changePassword(username, passwordEncoder.generatePasswordHash(password));

        return true;
    }

    @Override
    @Transactional
    public boolean changeIsActive(String username, boolean isActive) {
        if (username == null) {
            throw new ServiceException("Trainer username cannot be null");
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
    public List<TrainingType> getTrainingTypes() {
        return trainingTypeRepository.findAll();
    }
}
