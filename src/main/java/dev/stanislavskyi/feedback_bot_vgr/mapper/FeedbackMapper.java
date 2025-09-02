package dev.stanislavskyi.feedback_bot_vgr.mapper;

import dev.stanislavskyi.feedback_bot_vgr.dto.response.FeedbackAnalysisResponse;
import dev.stanislavskyi.feedback_bot_vgr.model.Feedback;
import dev.stanislavskyi.feedback_bot_vgr.model.SentimentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    @Mapping(target = "feedbackText", ignore = true)
    @Mapping(target = "sentiment", source = "sentiment", qualifiedByName = "mapSentiment")
    @Mapping(target = "resolution", source = "resolution", qualifiedByName = "listToString")
    Feedback toEntity(FeedbackAnalysisResponse response);

    @Named("mapSentiment")
    default SentimentType mapSentiment(String sentiment) {
        try {
            return SentimentType.valueOf(sentiment.toUpperCase());
        } catch (Exception e) {
            return SentimentType.NEUTRAL;
        }
    }


    @Named("listToString")
    default String listToString(List<String> list){
        if (list.isEmpty() || list == null) return "";
        return String.join("\n", list);
    }



}
