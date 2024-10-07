package com.zpi.model

import com.zpi.model.dto.*
import com.zpi.model.entity.*
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class Converter {

    companion object {

        @JvmStatic
        fun convertToStoryDTO(story: Story): PublishStoryDTO {
            return PublishStoryDTO(
                story.title,
                story.content,
                story.regDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                story.fkUser,
                story.prompt.ref
            )
        }

        @JvmStatic
        fun convertToStory(storyDTO: StoryDTO): Story {
            return Story(
                storyDTO.ref!!,
                storyDTO.title!!,
                storyDTO.username!!,
                storyDTO.content!!,
                Date(storyDTO.regDate)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime(),
                storyDTO.fkUser!!,
                Prompt(-1, storyDTO.genre.toString(), storyDTO.words!!.split(", "))
            )
        }

        @JvmStatic
        fun convertToStoryList(response: List<StoryDTO>?): MutableList<Story> {
            val stories: MutableList<Story> = mutableListOf()
            response!!.forEach { storyDTO ->
                stories.add(
                    Story(
                        storyDTO.ref!!,
                        storyDTO.title!!,
                        storyDTO.username!!,
                        storyDTO.content!!,
                        Date(storyDTO.regDate)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime(),
                        storyDTO.fkUser!!,
                        Prompt(-1, storyDTO.genre.toString(), storyDTO.words!!.split(", "))
                    ))
            }
            return stories
        }

        @JvmStatic
        fun convertToCommentDTO(comment: Comment): PublishCommentDTO {
            return PublishCommentDTO(
                comment.content,
                comment.regDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                comment.fkUser,
                comment.fkStory
            )
        }

        @JvmStatic
        fun convertToCommentList(response: List<CommentDTO>?): MutableList<Comment> {
            val comments: MutableList<Comment> = mutableListOf()
            response!!.forEach { commentDTO ->
                comments.add(
                    Comment(
                        commentDTO.commentRef!!,
                        commentDTO.content!!,
                        Date(commentDTO.regDate)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime(),
                        commentDTO.username!!,
                        commentDTO.profilePicture!!,
                        commentDTO.fkUser!!,
                        commentDTO.fkStory!!
                    )
                )
            }
            return comments
        }

        @JvmStatic
        fun convertToBannedWords(response: List<BannedWordDTO>?): MutableList<BannedWord> {
            val bannedWords: MutableList<BannedWord> = mutableListOf()
            response!!.forEach { bannedWordDTO ->
                bannedWords.add(
                    BannedWord(
                        bannedWordDTO.value!!,
                        bannedWordDTO.censored!!
                    )
                )
            }
            return bannedWords
        }

        @JvmStatic
        fun convertToGameStoryList(response: List<GameStoryDTO>?): MutableList<GameStory> {
            val stories: MutableList<GameStory> = mutableListOf()
            response!!.forEach { gameStoryDTO ->
                stories.add(
                    GameStory(
                        gameStoryDTO.ref!!,
                        gameStoryDTO.title!!,
                        gameStoryDTO.username!!,
                        gameStoryDTO.content!!,
                        Date(gameStoryDTO.regDate)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime(),
                        gameStoryDTO.fkUser!!,
                        Prompt(-1, gameStoryDTO.genre.toString(), gameStoryDTO.words!!.split(", ")),
                        gameStoryDTO.score!!
                    )
                )
            }
            return stories
        }

        @JvmStatic
        fun convertToGenreList(response: List<GenreDTO>?): MutableList<Genre> {
            val genres: MutableList<Genre> = mutableListOf()
            response!!.forEach { genreDTO ->
                genres.add(Genre(genreDTO.ref!!, genreDTO.genre!!))
            }
            return genres
        }

        @JvmStatic
        fun convertToPrompt(promptDTO: PromptDTO?): Prompt {
            return Prompt(promptDTO!!.ref!!, promptDTO.genre!!, promptDTO.words!!.split(", "))
        }

        @JvmStatic
        fun convertToScoreDTO(score: Score): ScoreDTO {
            return ScoreDTO(
                score.value,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                score.fkStory,
                score.fkUser
            )
        }

        @JvmStatic
        fun convertToStatistics(statsDTO: StatisticsDTO?): Statistics {
            return Statistics(
                statsDTO!!.stories!!,
                statsDTO.comments!!,
                statsDTO.favourites!!,
                statsDTO.favouritesOthers!!,
                statsDTO.commentsOthers!!,
                statsDTO.ratedOthers!!,
                statsDTO.wordsCount!!,
                statsDTO.avgWords!!,
                statsDTO.streak!!,
                statsDTO.minigameScore!!
            )
        }

        @JvmStatic
        fun convertToStoriesPerGenre(response: List<StoriesPerGenreDTO>?): MutableList<StoriesPerGenre> {
            val stories: MutableList<StoriesPerGenre> = mutableListOf()
            response!!.forEach { storiesPerGenreDTO ->
                stories.add(
                    StoriesPerGenre(
                        storiesPerGenreDTO.genre!!,
                        storiesPerGenreDTO.stories!!
                    ))
            }
            return stories
        }

        @JvmStatic
        fun convertToStoryAnalysis(response: StoryAnalysisDTO?): StoryAnalysis {
            val analysis = emptyAnalysis()
            analysis.correctnessPoints = response!!.correctnessPoints?.toDouble()
            analysis.genre = response.genre
            analysis.genrePoints = response.genrePoints?.toDouble()
            analysis.negativenessScore = response.negativenessScore
            analysis.positivenessScore = response.positivenessScore
            analysis.neutralnessScore = response.neutralnessScore
            analysis.promptCompletionPoints = response.promptCompletionPoints?.toDouble()
            analysis.vocabularyVarietyPoints = response.vocabularyVarietyPoints?.toDouble()
            return analysis
        }

        @JvmStatic
        fun convertToUserDTO(user: User, useMD5: Boolean): UserDTO {
            return UserDTO(
                user.ref,
                user.login,
                if (useMD5 && user.password != "") MD5.md5(user.password) else user.password,
                user.name,
                user.profilePicture,
                user.regDate.toString(),
                user.userType,
                user.email
            )
        }

        @JvmStatic
        fun convertToUser(userDTO: UserDTO): User {
            return User(userDTO.ref!!,
                userDTO.login!!,
                userDTO.password!!,
                userDTO.name!!,
                userDTO.profilePicture!!,
                Date(userDTO.regDate)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime(),
                userDTO.userType!!,
                userDTO.email!!
            )
        }

        @JvmStatic
        fun convertToLeaderboardList(response: List<LeaderboardItemDTO>?): MutableList<LeaderboardItem> {
            val leaderboard: MutableList<LeaderboardItem> = mutableListOf()
            response!!.forEach { leaderboardItemDTO ->
                leaderboard.add(
                    LeaderboardItem(
                        leaderboardItemDTO.ref!!,
                        leaderboardItemDTO.name!!,
                        leaderboardItemDTO.minigameScore!!
                    )
                )
            }
            return leaderboard
        }

        @JvmStatic
        private fun emptyAnalysis(): StoryAnalysis {
            return StoryAnalysis(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null)
        }
    }
}