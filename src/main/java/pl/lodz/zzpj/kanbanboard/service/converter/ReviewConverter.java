package pl.lodz.zzpj.kanbanboard.service.converter;

import pl.lodz.zzpj.kanbanboard.dto.review.ReviewDto;
import pl.lodz.zzpj.kanbanboard.entity.Review;

public class ReviewConverter {
    public static ReviewDto toDto(Review review){
        return new ReviewDto(
                review.getUuid(),
                review.getCreatedAt(),
                review.getReviewer().getEmail(),
                review.getComment(),
                review.isRejected()
        );
    }
}
