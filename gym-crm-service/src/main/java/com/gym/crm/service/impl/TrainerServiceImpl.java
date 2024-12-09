package com.gym.crm.service.impl;

import com.gym.crm.dto.TrainerProfile;
import com.gym.crm.dto.UpdateTrainerRequest;
import com.gym.crm.dto.UserDetailsResponse;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ServiceException;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.repository.TrainingTypeRepository;
import com.gym.crm.repository.UserRepository;
import com.gym.crm.security.CustomPasswordEncoder;
import com.gym.crm.service.TrainerService;
import com.gym.crm.specification.SearchCriteria;
import com.gym.crm.specification.SpecificationsBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;

@Service
@AllArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository repository;
    private final UserRepository userRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingRepository trainingRepository;
    private final ProfileService profileService;
    private final CustomPasswordEncoder passwordEncoder;

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

        return repository.findByUserUsername(username).orElseThrow(() -> new EntityNotFoundException(format("Trainer: %s not found in Repository", username)));
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
