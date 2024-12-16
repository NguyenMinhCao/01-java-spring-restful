package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.email.ResEmailJob;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository,
            SkillRepository skillRepository, JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public Optional<Subscriber> findSubscriberById(Long id) {
        return this.subscriberRepository.findById(id);
    }

    public Subscriber handleCreateSubscriber(Subscriber subscriber) {
        List<Skill> listSkill = new ArrayList<>();

        if (subscriber.getSkills().size() > 0) {
            for (Skill skill : subscriber.getSkills()) {
                Optional<Skill> findSkill = this.skillRepository.findById(skill.getId());
                if (findSkill.isPresent()) {
                    listSkill.add(findSkill.get());
                }
            }
        }

        subscriber.setSkills(listSkill);
        Subscriber subscriberNew = this.subscriberRepository.save(subscriber);
        return subscriberNew;
    }

    public Subscriber handleUpdateSubscriber(Subscriber subscriber, List<Skill> skills) {
        List<Skill> listSkill = new ArrayList<>();

        if (skills.size() > 0) {
            for (Skill skill : skills) {
                Optional<Skill> findSkill = this.skillRepository.findById(skill.getId());
                if (findSkill.isPresent()) {
                    listSkill.add(findSkill.get());
                }
            }
        }

        subscriber.setSkills(listSkill);

        return this.subscriberRepository.save(subscriber);
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    public List<Subscriber> findByEmail(String email) {
        List<Subscriber> subscriber = this.subscriberRepository.findByEmail(email);
        return subscriber;
    }

}
