package com.sigmat.lms.config;

import com.sigmat.lms.models.*;
import com.sigmat.lms.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionDataLoader implements CommandLineRunner {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public void run(String... args) throws Exception {
        if (subscriptionPlanRepository.count() == 0) {
            loadLearnerPlans();
            loadFacultyPlans();
        }
    }

    private void loadLearnerPlans() {
        // Foundation Plan (Free)
        SubscriptionPlan foundation = new SubscriptionPlan();
        foundation.setName("Foundation");
        foundation.setPlanType(SubscriptionPlanType.LEARNER);
        foundation.setLearnerTier(LearnerPlanTier.FOUNDATION);
        foundation.setPriceInr(BigDecimal.ZERO);
        foundation.setDescription("Access public course catalog, register, attend one free course, forum participation");
        foundation.setFeatures(Arrays.asList(
                "Access public course catalog",
                "Register for courses",
                "Attend one free course",
                "Forum participation"
        ));
        foundation.setBestSuitedFor("New users exploring the platform");
        foundation.setMinimumDurationMonths(1);

        // Essential Plan
        SubscriptionPlan essential = new SubscriptionPlan();
        essential.setName("Essential");
        essential.setPlanType(SubscriptionPlanType.LEARNER);
        essential.setLearnerTier(LearnerPlanTier.ESSENTIAL);
        essential.setPriceInr(new BigDecimal("849"));
        essential.setDescription("Full course access, assignment submission, discussion forums, certificate download");
        essential.setFeatures(Arrays.asList(
                "Full course access",
                "Assignment submission",
                "Discussion forums",
                "Certificate download"
        ));
        essential.setBestSuitedFor("Learners pursuing self-paced study");

        // Professional Plan
        SubscriptionPlan professional = new SubscriptionPlan();
        professional.setName("Professional");
        professional.setPlanType(SubscriptionPlanType.LEARNER);
        professional.setLearnerTier(LearnerPlanTier.PROFESSIONAL);
        professional.setPriceInr(new BigDecimal("1899"));
        professional.setDescription("Progress dashboards, instructor Q&A, downloadable content, mobile/offline access");
        professional.setFeatures(Arrays.asList(
                "Progress dashboards",
                "Instructor Q&A",
                "Downloadable content",
                "Mobile/offline access"
        ));
        professional.setBestSuitedFor("Working professionals");

        // Mastery Plan
        SubscriptionPlan mastery = new SubscriptionPlan();
        mastery.setName("Mastery");
        mastery.setPlanType(SubscriptionPlanType.LEARNER);
        mastery.setLearnerTier(LearnerPlanTier.MASTERY);
        mastery.setPriceInr(new BigDecimal("3799"));
        mastery.setDescription("Live webinar access, batch-based peer learning, instructor chat, resume-worthy certifications");
        mastery.setFeatures(Arrays.asList(
                "Live webinar access",
                "Batch-based peer learning",
                "Instructor chat",
                "Resume-worthy certifications"
        ));
        mastery.setBestSuitedFor("Goal-driven upskilling learners");

        // Institutional Plan (Custom)
        SubscriptionPlan institutional = new SubscriptionPlan();
        institutional.setName("Institutional");
        institutional.setPlanType(SubscriptionPlanType.LEARNER);
        institutional.setLearnerTier(LearnerPlanTier.INSTITUTIONAL);
        institutional.setPriceInr(BigDecimal.ZERO); // Custom pricing
        institutional.setDescription("Bulk enrollment, analytics reporting, learning pathways, compliance tracking");
        institutional.setFeatures(Arrays.asList(
                "Bulk enrollment",
                "Analytics reporting",
                "Learning pathways",
                "Compliance tracking"
        ));
        institutional.setBestSuitedFor("Academic institutions, corporates");
        institutional.setCustomPricing(true);

        subscriptionPlanRepository.saveAll(Arrays.asList(
                foundation, essential, professional, mastery, institutional
        ));
    }

    private void loadFacultyPlans() {
        // Starter Plan (Free)
        SubscriptionPlan starter = new SubscriptionPlan();
        starter.setName("Starter");
        starter.setPlanType(SubscriptionPlanType.FACULTY);
        starter.setFacultyTier(FacultyPlanTier.STARTER);
        starter.setPriceInr(BigDecimal.ZERO);
        starter.setDescription("Create one course, upload static content, track basic learner progress");
        starter.setFeatures(Arrays.asList(
                "Create one course",
                "Upload static content",
                "Track basic learner progress"
        ));
        starter.setBestSuitedFor("First-time creators");
        starter.setMinimumDurationMonths(1);

        // Educator Plan
        SubscriptionPlan educator = new SubscriptionPlan();
        educator.setName("Educator");
        educator.setPlanType(SubscriptionPlanType.FACULTY);
        educator.setFacultyTier(FacultyPlanTier.EDUCATOR);
        educator.setPriceInr(new BigDecimal("1299"));
        educator.setDescription("Multi-course authoring, quiz builder, grading tools, discussion management");
        educator.setFeatures(Arrays.asList(
                "Multi-course authoring",
                "Quiz builder",
                "Grading tools",
                "Discussion management"
        ));
        educator.setBestSuitedFor("Independent faculty and tutors");

        // Mentor Plan
        SubscriptionPlan mentor = new SubscriptionPlan();
        mentor.setName("Mentor");
        mentor.setPlanType(SubscriptionPlanType.FACULTY);
        mentor.setFacultyTier(FacultyPlanTier.MENTOR);
        mentor.setPriceInr(new BigDecimal("2599"));
        mentor.setDescription("Live session hosting, batch certificate generation, feedback analytics, learner messaging");
        mentor.setFeatures(Arrays.asList(
                "Live session hosting",
                "Batch certificate generation",
                "Feedback analytics",
                "Learner messaging"
        ));
        mentor.setBestSuitedFor("Subject experts & exam trainers");

        // Institutional Plan (Custom)
        SubscriptionPlan institutionalFaculty = new SubscriptionPlan();
        institutionalFaculty.setName("Institutional");
        institutionalFaculty.setPlanType(SubscriptionPlanType.FACULTY);
        institutionalFaculty.setFacultyTier(FacultyPlanTier.INSTITUTIONAL);
        institutionalFaculty.setPriceInr(new BigDecimal("4999"));
        institutionalFaculty.setDescription("Role-based access for teams, institution-wide reporting, secure backups, dedicated success manager");
        institutionalFaculty.setFeatures(Arrays.asList(
                "Role-based access for teams",
                "Institution-wide reporting",
                "Secure backups",
                "Dedicated success manager"
        ));
        institutionalFaculty.setBestSuitedFor("Universities, EdTech partners");

        subscriptionPlanRepository.saveAll(Arrays.asList(
                starter, educator, mentor, institutionalFaculty
        ));
    }
}