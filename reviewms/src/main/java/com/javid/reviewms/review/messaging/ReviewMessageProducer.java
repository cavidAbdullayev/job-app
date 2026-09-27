package com.javid.reviewms.review.messaging;

import com.javid.reviewms.review.entity.Review;
import com.javid.reviewms.review.repository.ReviewRepository;
import com.javid.reviewms.review.dto.messaging.ReviewMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReviewMessageProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ReviewRepository reviewRepository;

    public ReviewMessageProducer(RabbitTemplate rabbitTemplate,
                                 ReviewRepository reviewRepository){
        this.rabbitTemplate = rabbitTemplate;
        this.reviewRepository = reviewRepository;
    }

    public void sendMessage(Long companyId){
        Double averageRating = reviewRepository.findByCompanyId(companyId).stream().mapToDouble(Review::getRating).average().orElse(0.0);
        ReviewMessage reviewMessage = new ReviewMessage(
              averageRating,
                companyId
        );
          rabbitTemplate.convertAndSend("companyRatingQueue", reviewMessage);
    }


}
