package com.fiap.nova.service;

import com.fiap.nova.model.Skill;
import com.fiap.nova.model.SkillType;
import com.fiap.nova.model.User;
import com.fiap.nova.repository.SkillRepository;
import com.fiap.nova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public Page<Skill> listSkillsByUser(Long userId, Pageable pageable) {
        if (userId == null) {
            return Page.empty();
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        List<Skill> userSkills = user.getSkills();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userSkills.size());
        
        List<Skill> pageContent = (start > userSkills.size()) ? List.of() : userSkills.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, userSkills.size());
    }

    public Skill createSkill(Skill skill, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String skillName = skill.getName().trim();

        Optional<Skill> existingSkill = skillRepository.findByNameIgnoreCase(skillName);
        
        Skill skillToLink;
        if (existingSkill.isPresent()) {
            skillToLink = existingSkill.get();
        } else {
            skill.setName(skillName); 
            if (skill.getType() == null) {
                skill.setType(SkillType.HARD); 
            }
            skillToLink = skillRepository.save(skill);
        }

        if (!user.getSkills().contains(skillToLink)) {
            user.getSkills().add(skillToLink);
            userRepository.save(user);
        }

        return skillToLink;
    }

    public List<Skill> listAll() {
        return skillRepository.findAll();
    }

    public Page<Skill> listAllPaginated(Pageable pageable) {
        return skillRepository.findAll(pageable);
    }

    public Skill getSkillById(Long id) {
        return skillRepository.findById(id).orElse(null);
    }

    public Skill updateSkill(Long skillId, Long userId, Skill skillDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Skill skill = getSkillById(skillId);
        if (skill == null) {
            throw new RuntimeException("Skill not found: " + skillId);
        }
        
        // Verifica se a skill pertence ao usuário
        if (!user.getSkills().contains(skill)) {
            throw new RuntimeException("Skill does not belong to user: " + userId);
        }
        
        user.getSkills().remove(skill);
        
        String newSkillName = skillDetails.getName().trim();
        Optional<Skill> existingSkill = skillRepository.findByNameIgnoreCase(newSkillName);
        
        Skill skillToLink;
        if (existingSkill.isPresent() && !existingSkill.get().getId().equals(skillId)) {
            skillToLink = existingSkill.get();
        } else {
            skill.setName(newSkillName);
            if (skillDetails.getType() != null) {
                skill.setType(skillDetails.getType());
            }
            skillToLink = skillRepository.save(skill);
        }
        
        if (!user.getSkills().contains(skillToLink)) {
            user.getSkills().add(skillToLink);
        }
        userRepository.save(user);
        
        return skillToLink;
    }
    
    public void removeSkillFromUser(Long userId, Long skillId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Skill skill = getSkillById(skillId);
        if (skill != null) {
            if (user.getSkills().remove(skill)) {
                userRepository.save(user);
            }
        }
    }
    
    public void deleteSkill(Long skillId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Skill skill = getSkillById(skillId);
        if (skill == null) {
            throw new RuntimeException("Skill not found: " + skillId);
        }
        
        // Verifica se a skill pertence ao usuário
        if (!user.getSkills().contains(skill)) {
            throw new RuntimeException("Skill does not belong to user: " + userId);
        }
        
        // Remove a skill do usuário
        user.getSkills().remove(skill);
        userRepository.save(user);
    }
}