import hashlib
import os
import random
import string
from datetime import datetime
import psycopg2
import pyodbc
import requests
from flask import Flask, request, render_template

from model import *

app = Flask(__name__)
server = 'ec2-54-228-125-183.eu-west-1.compute.amazonaws.com'
database = 'd2rkqh8o5kq4rh'
username = 'gagjcfsdzmymmr'
password = '{bdbecadf9de2bbb0e5f8682d43a9d41758c24b75268b1443ae8c270c1bd79391}'
driver = '{PostgreSQL Unicode(x64)}'
ssl = 'sslmode=require'
port = '5432'

DATABASE_URL = os.environ['DATABASE_URL']


def start_analyse(story_ref: str, story_content: str, fk_prompt: str):
    genre = _get_prompt_genre(fk_prompt)
    prompt_words = _get_prompt_words(fk_prompt)
    url = "https://zpi-ai.herokuapp.com/analyse"
    payload = {
        "story_ref": str(story_ref),
        "story_content": str(story_content),
        "fk_prompt": str(fk_prompt),
        "prompt_genre": str(genre),
        "prompt_words": str(prompt_words)
    }
    headers = {
        "content-type": "application/json"
    }
    print(url)
    try:
        requests.request("POST", url, json=payload, headers=headers, timeout=1)
    except requests.exceptions.ReadTimeout:
        pass


def start_generate(story_ref: str, user_ref: str, fk_prompt: str, story_content: str):
    genre = _get_prompt_genre(fk_prompt)
    prompt_words = _get_prompt_words(fk_prompt)
    url = "https://zpi-gen.herokuapp.com/generate"
    payload = {
        "story_ref": str(story_ref),
        "story_content": str(story_content),
        "user_ref": str(user_ref),
        "fk_prompt": str(fk_prompt),
        "prompt_genre": str(genre),
        "prompt_words": str(prompt_words)
    }
    headers = {
        "content-type": "application/json"
    }
    print(url)
    try:
        requests.request("POST", url, json=payload, headers=headers, timeout=1)
    except requests.exceptions.ReadTimeout:
        pass


def _get_prompt_genre(fk_prompt: str):
    try:
        query = "select gd.value from genresdict gd " \
                "inner join prompts p on p.fk_genre = gd.ref " \
                "where p.ref = " + str(fk_prompt)
        with _get_connection() as conn:
            with conn.cursor() as cursor:
                cursor.execute(query)
                row = cursor.fetchone()
                return row[0]
    except Exception as e:
        print(e)


def _get_prompt_words(fk_prompt):
    try:
        query = "select replace(trim(trailing ',' from string_agg(wd.value, ', ')), ' ', '') as words " \
                "from wordsdict wd " \
                "inner join promptswords pw on pw.fk_word = wd.ref " \
                "where pw.fk_prompt = " + str(fk_prompt)
        with _get_connection() as conn:
            with conn.cursor() as cursor:
                cursor.execute(query)
                row = cursor.fetchone()
                return row[0]
    except Exception as e:
        print(e)


def _escape_specials(text: str):
    return text.replace("'", "''")


def _get_connection():
    return psycopg2.connect(DATABASE_URL, sslmode='require')


def _get_connection_local():
    return pyodbc.connect('DRIVER=' + driver +
                          ';SERVER=' + server +
                          ';PORT=' + port +
                          ';DATABASE=' + database +
                          ';UID=' + username +
                          ';PWD=' + password +
                          ';' + ssl)


# done
def _get_user_stories(user_ref: str):
    query = "select s.ref, s.title, s.content, s.regdate as reg_date, s.fk_user, s.fk_prompt, u.name as username, " \
            "gd.value as genre, trim(trailing ', ' from string_agg(wd.value, ', ')) as words " \
            "from stories s " \
            "inner join users u on u.ref = s.fk_user " \
            "inner join prompts p on p.ref = s.fk_prompt " \
            "inner join genresdict gd on gd.ref = p.fk_genre " \
            "inner join promptswords pw on pw.fk_prompt = p.ref " \
            "inner join wordsdict wd on wd.ref = pw.fk_word " \
            "where u.ref = " + user_ref + " and s.generatedfor is null " \
            "group by s.ref, s.title, s.content, u.name, gd.value " \
            "order by s.regdate desc"
    records = []
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            for row in rows:
                records.append(dict(zip(attributes, row)))
            return records


# done
def _get_stories(user_ref: str):
    query = "select s.ref, s.title, s.content, s.regdate as reg_date, s.fk_user, s.fk_prompt, u.name as username, " \
            "gd.value as genre, trim(trailing ', ' from string_agg(wd.value, ', ')) as words " \
            "from stories s " \
            "inner join users u on u.ref = s.fk_user " \
            "inner join prompts p on p.ref = s.fk_prompt " \
            "inner join genresdict gd on gd.ref = p.fk_genre " \
            "inner join promptswords pw on pw.fk_prompt = p.ref " \
            "inner join wordsdict wd on wd.ref = pw.fk_word " \
            "where u.ref != " + user_ref + " and s.generatedfor is null " \
            "group by s.ref, s.title, s.content, u.name, gd.value " \
            "order by s.regdate desc"
    records = []
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            for row in rows:
                records.append(dict(zip(attributes, row)))
            return records


# done
def _get_user_favourites(user_ref: str):
    query = "select s.ref, s.title, s.content, s.regdate as reg_date, s.fk_user, s.fk_prompt, u.name as username, " \
            "gd.value as genre, trim(trailing ', ' from string_agg(wd.value, ', ')) as words " \
            "from favourites f " \
            "inner join stories s on s.ref = f.fk_story " \
            "inner join users u on u.ref = s.fk_user " \
            "inner join prompts p on p.ref = s.fk_prompt " \
            "inner join genresdict gd on gd.ref = p.fk_genre " \
            "inner join promptswords pw on pw.fk_prompt = p.ref " \
            "inner join wordsdict wd on wd.ref = pw.fk_word " \
            "where f.fk_user = " + user_ref + " " \
            "group by s.ref, s.title, s.content, u.name, gd.value, f.regdate " \
            "order by f.regdate"
    records = []
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            for row in rows:
                records.append(dict(zip(attributes, row)))
            return records


# done
@app.route("/")
def home():
    return {"message": "ok"}


# done
# if it randomly stops working its possible you have to cleanup the database
@app.route("/generateChallenge/<value>")
def generate_challenge(value: str):
    no_of_words = int(value)
    query_genres = "select ref, value from genresdict"
    query_words = "select ref, value from wordsdict"
    genre_list = []
    word_list = []
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query_genres)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            for row in rows:
                genre_list.append(dict(zip(attributes, row)))
            cursor.execute(query_words)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            for row in rows:
                word_list.append(dict(zip(attributes, row)))
    genre = random.choice(genre_list)
    words = random.sample(word_list, no_of_words)
    words_str = ""
    for word in words:
        words_str += word["value"] + ", "
    words_str = words_str.rstrip(", ")
    select_query = "select p.ref, gd.value, trim(trailing ', ' from string_agg(wd.value, ', ')) " \
                   "from prompts p " \
                   "inner join genresdict gd on gd.ref = p.fk_genre " \
                   "inner join promptswords pw on p.ref = pw.fk_prompt " \
                   "inner join wordsdict wd on pw.fk_word = wd.ref " \
                   "where gd.value = '" + genre["value"] + "' " \
                   "group by p.ref, gd.value " \
                   "having trim(trailing ', ' from string_agg(wd.value, ', ')) = '" + words_str + "'"
    insert_prompt = "insert into prompts (fk_genre) " \
                    "values (" + str(genre["ref"]) + ") " \
                    "returning ref"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(select_query)
            row = cursor.fetchone()
            if row is not None:
                return {"ref": row[0], "genre": row[1], "words": row[2]}
            else:
                cursor.execute(insert_prompt)
                row = cursor.fetchone()
                prompt_ref = row[0]
                insert_prompts_words = "insert into promptswords (fk_prompt, fk_word) values "
                for word in words:
                    insert_prompts_words += "(" + str(prompt_ref) + ", " + str(word["ref"]) + "), "
                insert_prompts_words = insert_prompts_words.rstrip(", ")
                insert_prompts_words += " returning ref"
                cursor.execute(insert_prompts_words)
                cursor.execute(select_query)
                row = cursor.fetchone()
                return {"ref": row[0], "genre": row[1], "words": row[2]}


# done
@app.route("/publishStory", methods=['POST'])
def publish_story():
    try:
        story = Story(request.get_json())
        query = "insert into stories (title, content, regdate, fk_user, fk_prompt) " \
                "values ('" + _escape_specials(story.title) + "', '" + _escape_specials(story.content) + "', '" \
                + story.reg_date + "', " + str(story.fk_user) + ", " + str(story.fk_prompt) + ") " \
                "returning ref"
        with _get_connection() as conn:
            with conn.cursor() as cursor:
                cursor.execute(query)
                row = cursor.fetchone()
                start_analyse(str(row[0]), story.content, str(story.fk_prompt))
                start_generate(str(row[0]), str(story.fk_user), str(story.fk_prompt), str(story.content))
                return {"ref": row[0]}
    except Exception as e:
        print(e)


# done
@app.route("/removeStory/<story_ref>")
def remove_story(story_ref: str):
    query_stats_ai = "delete from statsai where fk_story = " + story_ref
    query_comments = "delete from comments where fk_story = " + story_ref
    query_user_scores = "delete from userscores where fk_story = " + story_ref
    query_favourites = "delete from favourites where fk_story = " + story_ref
    query_generated = "update stories set generatedfor = -1 where generatedfor = " + story_ref
    query_stories = "delete from stories where ref = " + story_ref
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query_stats_ai)
            cursor.execute(query_comments)
            cursor.execute(query_user_scores)
            cursor.execute(query_favourites)
            cursor.execute(query_generated)
            cursor.execute(query_stories)
            return {"message": "ok"}


# done
@app.route("/getUserRef/<login>")
def get_user_ref(login: str):
    query = "select ref from users where login='" + login + "'"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return {"ref": row[0]}


# done
@app.route("/getUserPassword/<login>")
def get_user_password(login: str):
    query = "select ref, password from users where login = '" + login + "'"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            if row is not None:
                return {"ref": row[0], "password": row[1]}
            else:
                return {"ref": -1, "password": ""}


# done
@app.route("/getUserEmail/<login>")
def get_user_email(login: str):
    query = "select ref, email from users where login = '" + login + "'"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            if row is not None:
                return {"ref": row[0], "email": row[1]}
            else:
                return {"ref": -1, "email": ""}


# done
@app.route("/getUserLogins/<email>")
def get_user_logins(email: str):
    query = "select trim(trailing ', ' from string_agg(login, ', ')) as logins from users where email = '" + email + "'"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            if row is not None and row[0] is not None:
                return {"logins": row[0]}
            else:
                return {"logins": ""}


# done
@app.route("/registerUser", methods=['POST'])
def register_user():
    user = User(request.get_json())
    query = "insert into users (login, password, name, profilepicture, regdate, usertype, email) " \
            "values ('" + _escape_specials(user.login) + "', '" + user.password + "', '" \
            + _escape_specials(user.name) + "', " + str(user.profile_pic) + ", '" + user.reg_date + "', " \
            + str(user.user_type) + ", '" + user.email + "') " \
            "returning ref"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return {"ref": row[0]}


# done
@app.route("/getBannedWords")
def get_banned_words():
    query = "select ref, value, censored from bannedwords"
    records = []
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            for row in rows:
                records.append(dict(zip(attributes, row)))
            return json.dumps(records)


# done
# prev_date is used so ignore the warning
@app.route("/getUserStatistics/<user_ref>")
def get_user_statistics(user_ref: str):
    query_stories = "select count(ref) from stories where fk_user = " + user_ref + " and generatedfor is null"
    query_comments = "select count(ref) from comments where fk_user = " + user_ref
    query_minigame = "select coalesce(sum(correct), 0) from minigame where fk_user = " + user_ref
    query_comments_by_other = "select count(c.ref) " \
                              "from comments c " \
                              "inner join stories s on s.ref = c.fk_story " \
                              "where s.fk_user = " + user_ref
    query_favourites_by_other = "select count(f.ref) " \
                                "from favourites f " \
                                "inner join stories s on s.ref = f.fk_story " \
                                "where s.fk_user = " + user_ref
    query_rated_by_other = "select count(us.ref) " \
                           "from userscores us " \
                           "inner join stories s on s.ref = us.fk_story " \
                           "where s.fk_user = " + user_ref
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query_stories)
            row = cursor.fetchone()
            stories_count = row[0]
            cursor.execute(query_comments)
            row = cursor.fetchone()
            comments_count = row[0]
            cursor.execute(query_minigame)
            row = cursor.fetchone()
            minigame_score = row[0]
            cursor.execute(query_comments_by_other)
            row = cursor.fetchone()
            comments_by_other = row[0]
            cursor.execute(query_favourites_by_other)
            row = cursor.fetchone()
            favourites_by_other = row[0]
            cursor.execute(query_rated_by_other)
            row = cursor.fetchone()
            rated_by_other = row[0]
            stories = _get_user_stories(user_ref)
            stories.sort(key=lambda s: s["reg_date"])
            words_count = 0
            streak = 0
            avg_words = 0
            prev_date = None
            if stories:
                for story in stories:
                    if prev_date is None:
                        prev_date = story["reg_date"]
                        streak += 1
                    else:
                        delta = story["reg_date"] - prev_date
                        if delta.days == 1:
                            streak += 1
                    words = story["content"].split(" ")
                    words_count += len(words)
                    prev_date = story["reg_date"]
                avg_words = words_count / len(stories)
            favourites = _get_user_favourites(user_ref)
            return {"stories": stories_count, "comments": comments_count, "words_count": words_count,
                    "avg_words": round(avg_words, 2), "streak": streak, "minigame_score": minigame_score,
                    "favourites_count": len(favourites), "comments_by_other": comments_by_other,
                    "favourites_by_other": favourites_by_other, "rated_by_other": rated_by_other}


# done
@app.route("/getUserStatisticsPerGenre/<user_ref>")
def get_user_statistics_per_genre(user_ref: str):
    query = "select gd.value as genre, count(s.ref) as stories " \
            "from stories s " \
            "inner join prompts p on p.ref = s.fk_prompt " \
            "inner join genresdict gd on gd.ref = p.fk_genre " \
            "where fk_user = " + user_ref + " " \
            "and generatedfor is null " \
            "group by gd.value"
    records = []
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            for row in rows:
                records.append(dict(zip(attributes, row)))
            return json.dumps(records)


# done
@app.route("/getGenres")
def get_genres():
    query = "select ref, value from genresdict"
    records = []
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            for row in rows:
                records.append(dict(zip(attributes, row)))
            return json.dumps(records)


# done
@app.route("/getStories/<user_ref>")
def get_stories(user_ref: str):
    records = _get_stories(user_ref)
    return json.dumps(records)


# done
@app.route("/search/<query>")
def search_stories(query: str):
    query = "select s.ref, s.title, s.content, s.regdate as reg_date, s.fk_user, s.fk_prompt, u.name as username, " \
            "gd.value as genre, trim(trailing ', ' from string_agg(wd.value, ', ')) as words " \
            "from stories s " \
            "inner join users u on u.ref = s.fk_user " \
            "inner join prompts p on p.ref = s.fk_prompt " \
            "inner join genresdict gd on gd.ref = p.fk_genre " \
            "inner join promptswords pw on pw.fk_prompt = p.ref " \
            "inner join wordsdict wd on wd.ref = pw.fk_word " \
            "where s.generatedfor is null " \
            "and (s.title like '%' || '" + _escape_specials(query) + "' || '%'  " \
            "or s.content like '%' || '" + _escape_specials(query) + "' || '%' " \
            "or u.name like '%' || '" + _escape_specials(query) + "' || '%') " \
            "group by s.ref, s.title, s.content, u.name, gd.value " \
            "order by s.regdate desc"
    records = []
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            for row in rows:
                records.append(dict(zip(attributes, row)))
            return json.dumps(records)


# done
@app.route("/getUserStories/<user_ref>")
def get_user_stories(user_ref: str):
    records = _get_user_stories(user_ref)
    return json.dumps(records)


# done
@app.route("/getUser/<user_ref>")
def get_user(user_ref: str):
    query = "select ref, login, password, name, profilepicture, regdate, usertype, email " \
            "from users " \
            "where ref = " + user_ref
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return {"ref": row[0], "login": row[1], "password": row[2], "name": row[3], "profile_pic": row[4],
                    "reg_date": row[5], "user_type": row[6], "email": row[7]}


# done
@app.route("/getUserFavourites/<user_ref>")
def get_user_favourites(user_ref: str):
    records = _get_user_favourites(user_ref)
    return json.dumps(records)


# done
@app.route("/getFavouriteRef/<user_ref>/<story_ref>")
def get_favourite_ref(user_ref: str, story_ref: str):
    query = "select ref from favourites where fk_user = " + user_ref + " and fk_story = " + story_ref
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            if row is not None:
                return {"ref": row[0]}
            else:
                return {"ref": 0}


# done
@app.route("/addFavourite/<user_ref>/<story_ref>")
def add_favourite(user_ref: str, story_ref: str):
    query = "insert into favourites (regdate, fk_user, fk_story) " \
            "values (current_timestamp, " + user_ref + ", " + story_ref + ") " \
            "returning ref"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return {"ref": row[0]}


# done
@app.route("/removeFavourite/<fav_ref>")
def remove_favourite(fav_ref: str):
    query = "delete from favourites where ref = " + fav_ref
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            return {"message": "ok"}


# done
@app.route("/getStoryComments/<story_ref>")
def get_story_comments(story_ref: str):
    query = "select c.ref, c.content, c.regdate as reg_date, c.fk_story, c.fk_user, u.name, " \
            "u.profilepicture as profile_pic " \
            "from comments c " \
            "inner join users u on u.ref = c.fk_user " \
            "where c.fk_story = " + story_ref + " " \
            "order by c.regdate"
    records = []
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            for row in rows:
                records.append(dict(zip(attributes, row)))
            return json.dumps(records)


# done
@app.route("/getStoryAnalysis/<story_ref>")
def get_story_analysis(story_ref: str):
    try:
        query = "select correctness, genreaccuracy, promptcompletion, vocabvariety, " \
                "positiveness, negativeness, neutralness, genre " \
                "from statsai where fk_story = " + story_ref
        with _get_connection() as conn:
            with conn.cursor() as cursor:
                cursor.execute(query)
                row = cursor.fetchone()
                print(row)
                return {"correctness_points": row[0], "genre_points": row[1], "prompt_completion_points": row[2],
                        "vocabulary_variety_points": row[3], "positiveness_score": row[4],
                        "negativeness_score": row[5], "neutralness_score": row[6], "genre": row[7]}
    except Exception as e:
        print(e)


# done
@app.route("/getGeneratedStory/<story_ref>")
def get_generated_story(story_ref: str):
    query = "select s.ref, s.title, s.content, s.regdate as reg_date, s.fk_user, s.fk_prompt, u.name as username, " \
            "gd.value as genre, trim(trailing ', ' from string_agg(wd.value, ', ')) as words " \
            "from stories s " \
            "inner join users u on u.ref = s.fk_user " \
            "inner join prompts p on p.ref = s.fk_prompt " \
            "inner join genresdict gd on gd.ref = p.fk_genre " \
            "inner join promptswords pw on pw.fk_prompt = p.ref " \
            "inner join wordsdict wd on wd.ref = pw.fk_word " \
            "where s.generatedfor = " + story_ref + " " \
            "group by s.ref, s.title, s.content, u.name, gd.value"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return {"ref": row[0], "title": row[1], "content": row[2], "reg_date": row[3], "fk_user": row[4],
                    "fk_story": row[5], "username": row[6], "genre": row[7], "words": row[8]}


# done
@app.route("/removeComment/<comment_ref>")
def remove_comment(comment_ref: str):
    query = "delete from comments where ref = " + comment_ref
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            return {"message": "ok"}


# done
@app.route("/publishComment", methods=['POST'])
def publish_comment():
    comment = Comment(request.get_json())
    query = "insert into comments (content, regdate, fk_user, fk_story) " \
            "values ('" + _escape_specials(comment.content) + "', '" + comment.reg_date + "', " + str(comment.fk_user) \
            + ", " + str(comment.fk_story) + ") " \
            "returning ref"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return {"ref": row[0]}


# done
@app.route("/changePassword", methods=["PUT"])
def change_password():
    new_user = NewPassword(request.get_json())
    query = "update users " \
            "set password = '" + new_user.password + "' " \
            "where ref = " + str(new_user.user_ref) + " " \
            "returning ref, login, password, name, profilepicture, regdate, usertype, email"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return {"ref": row[0], "login": row[1], "password": row[2], "name": row[3], "profile_pic": row[4],
                    "reg_date": row[5], "user_type": row[6], "email": row[7]}


# done
@app.route("/changeUsername", methods=["PUT"])
def change_username():
    new_user = User(request.get_json())
    query = "update users " \
            "set name = '" + _escape_specials(new_user.name) + "' " \
            "where ref = " + str(new_user.ref) + " " \
            "returning ref, login, password, name, profilepicture, regdate, usertype, email"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return {"ref": row[0], "login": row[1], "password": row[2], "name": row[3], "profile_pic": row[4],
                    "reg_date": row[5], "user_type": row[6], "email": row[7]}


# done
@app.route("/changeProfilePicture", methods=["PUT"])
def change_profile_picture():
    new_user = User(request.get_json())
    query = "update users " \
            "set profilepicture = " + str(new_user.profile_pic) + " " \
            "where ref = " + str(new_user.ref) + " " \
            "returning ref, login, password, name, profilepicture, regdate, usertype, email"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return {"ref": row[0], "login": row[1], "password": row[2], "name": row[3], "profile_pic": row[4],
                    "reg_date": row[5], "user_type": row[6], "email": row[7]}


# done
@app.route("/changeEmail", methods=["PUT"])
def change_email():
    new_user = User(request.get_json())
    query = "update users " \
            "set email = '" + new_user.email + "' " \
            "where ref = " + str(new_user.ref) + " " \
            "returning ref, login, password, name, profilepicture, regdate, usertype, email"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return {"ref": row[0], "login": row[1], "password": row[2], "name": row[3], "profile_pic": row[4],
                    "reg_date": row[5], "user_type": row[6], "email": row[7]}


# done
@app.route("/getAverageScore/<story_ref>")
def get_average_score(story_ref: str):
    query = "select avg(value) from userscores where fk_story = " + story_ref
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            if row[0] is not None:
                return {"avg": row[0]}
            else:
                return {"avg": -1}


# done
@app.route("/getScore/<story_ref>/<user_ref>")
def get_score(story_ref: str, user_ref: str):
    query = "select ref, value from userscores where fk_user = " + user_ref + " and fk_story = " + story_ref
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            if row is not None:
                return {"ref": row[0], "value": row[1]}
            else:
                return {"ref": -1, "value": -1}


# done
@app.route("/addScore", methods=['POST'])
def add_score():
    score = Score(request.get_json())
    select_query = "select ref from userscores " \
                   "where fk_story = " + str(score.fk_story) + " and fk_user = " + str(score.fk_user)
    insert_query = "insert into userscores (value, regdate, fk_story, fk_user) " \
                   "values ('" + str(score.value) + "', '" + score.reg_date + "', " + str(score.fk_story) \
                   + ", " + str(score.fk_user) + ") " \
                   "returning ref, value, regdate, fk_story, fk_user"
    update_query = "update userscores " \
                   "set value = " + str(score.value) + ", regdate = '" + score.reg_date + "' " \
                   "where fk_story = " + str(score.fk_story) + " " \
                   "and fk_user = " + str(score.fk_user) + " " \
                   "returning ref, value, regdate, fk_story, fk_user"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(select_query)
            row = cursor.fetchone()
            if row is not None:
                cursor.execute(update_query)
                row = cursor.fetchone()
                return {"ref": row[0], "value": row[1], "reg_date": row[2], "fk_story": row[3], "fk_user": row[4]}
            else:
                cursor.execute(insert_query)
                row = cursor.fetchone()
                return {"ref": row[0], "value": row[1], "reg_date": row[2], "fk_story": row[3], "fk_user": row[4]}


# done
@app.route("/getLeaderboard")
def get_leaderboard():
    query = "select u.ref, u.name, coalesce(sum(correct), 0) as minigame_score " \
            "from minigame m " \
            "join users u on u.ref = m.fk_user " \
            "group by u.ref, u.name " \
            "order by 3 desc " \
            "limit 5"
    records = []
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            for row in rows:
                records.append(dict(zip(attributes, row)))
            return json.dumps(records)


# done
@app.route("/addMinigameAnswer", methods=['POST'])
def add_minigame_answer():
    answer = MinigameAnswer(request.get_json())
    query = "insert into minigame (correct, regdate, fk_story, fk_user) " \
            "values (" + str(answer.correct) + ", '" + answer.reg_date + "', " + str(answer.fk_story) + ", " \
            + str(answer.fk_user) + ") " \
            "returning ref"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return {"ref": row[0]}


# done
@app.route("/getMinigameItem/<genre_ref>/<user_ref>")
def get_minigame_item(genre_ref: str, user_ref: str):
    ai_query = "select s.ref, s.title, s.content, s.regdate as reg_date, s.fk_user, s.fk_prompt, u.name as username, " \
               "gd.value as genre, trim(trailing ', ' from string_agg(wd.value, ', ')) as words, " \
               "sa.correctness + sa.genreaccuracy + sa.promptcompletion + sa.vocabvariety as score " \
               "from stories s " \
               "inner join users u on u.ref = s.fk_user " \
               "inner join prompts p on p.ref = s.fk_prompt " \
               "inner join genresdict gd on gd.ref = p.fk_genre " \
               "inner join promptswords pw on pw.fk_prompt = p.ref " \
               "inner join wordsdict wd on wd.ref = pw.fk_word " \
               "inner join statsai sa on s.ref = sa.fk_story " \
               "where u.ref != " + user_ref + " " \
               "and s.ref not in (select m.fk_story from minigame m where m.fk_user = " + user_ref + ") " \
               "and gd.ref = " + genre_ref + " " \
               "and s.generatedfor is not null " \
               "and sa.correctness is not null " \
               "and sa.genreaccuracy is not null " \
               "and sa.promptcompletion is not null " \
               "and sa.vocabvariety is not null " \
               "and sa.positiveness is not null " \
               "and sa.negativeness is not null " \
               "and sa.neutralness is not null " \
               "and sa.genre is not null " \
               "group by 1, 7, 8, 10"
    records = []
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(ai_query)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            if not rows:
                return {}
            for row in rows:
                records.append(dict(zip(attributes, row)))
            ai_story = random.choice(records)
            records = []
            ai_ref = ai_story["ref"]
            ref_query = "select generatedfor from stories where ref = " + str(ai_ref)
            cursor.execute(ref_query)
            row = cursor.fetchone()
            generated_for = row[0]
            story_query = "select s.ref, s.title, s.content, s.regdate as reg_date, s.fk_user, s.fk_prompt, " \
                          "u.name as username, gd.value as genre, " \
                          "trim(trailing ', ' from string_agg(wd.value, ', ')) as words, " \
                          "sa.correctness + sa.genreaccuracy + sa.promptcompletion + sa.vocabvariety as score " \
                          "from stories s " \
                          "inner join users u on u.ref = s.fk_user " \
                          "inner join prompts p on p.ref = s.fk_prompt " \
                          "inner join genresdict gd on gd.ref = p.fk_genre " \
                          "inner join promptswords pw on pw.fk_prompt = p.ref " \
                          "inner join wordsdict wd on wd.ref = pw.fk_word " \
                          "inner join statsai sa on s.ref = sa.fk_story " \
                          "where s.ref = " + str(generated_for) + " " \
                          "and sa.correctness is not null " \
                          "and sa.genreaccuracy is not null " \
                          "and sa.promptcompletion is not null " \
                          "and sa.vocabvariety is not null " \
                          "and sa.positiveness is not null " \
                          "and sa.negativeness is not null " \
                          "and sa.neutralness is not null " \
                          "and sa.genre is not null " \
                          "group by 1, 7, 8, 10"
            cursor.execute(story_query)
            attributes = [key[0] for key in cursor.description]
            rows = cursor.fetchall()
            if not rows:
                return {}
            for row in rows:
                records.append(dict(zip(attributes, row)))
            records.append(ai_story)
            return json.dumps(records)


# done
@app.route("/resetPassword/<user_ref>")
def reset_password(user_ref: str):
    characters = string.ascii_letters + string.digits + string.punctuation
    new_password = ''.join(random.choice(characters) for _ in range(12))
    new_password = new_password.lower()
    md5 = hashlib.md5(new_password.encode()).hexdigest()
    query = "update users " \
            "set password = '" + md5 + "' " \
            "where ref = " + user_ref + " " \
            "returning login, name"
    with _get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(query)
            row = cursor.fetchone()
            return render_template('reset_password.html', login=row[0], name=row[1], password=new_password)


# done
@app.route("/publishGenerated", methods=['POST'])
def publish_generated():
    try:
        story = StoryGenerated(request.get_json())
        print("generate for " + story.generated_for)
        query = "insert into stories (title, content, regdate, fk_user, fk_prompt, generatedfor) " \
                "values ('generated' , '" + _escape_specials(story.content) + "' , current_timestamp, " \
                + str(story.fk_user) + ", " + str(story.fk_prompt) + " , " + str(story.generated_for) + " ) " \
                "returning ref"
        with _get_connection() as conn:
            with conn.cursor() as cursor:
                cursor.execute(query)
                row = cursor.fetchone()
                start_analyse(str(row[0]), story.content, str(story.fk_prompt))
                return {"ref": row[0]}
    except Exception as e:
        print(e)


# done
@app.route("/updateDate")
def update_date():
    try:
        query = "insert into localgeneration (regdate) " \
                "values (current_timestamp) " \
                "returning ref"
        with _get_connection() as conn:
            with conn.cursor() as cursor:
                cursor.execute(query)
                row = cursor.fetchone()
                return {"ref": row[0]}
    except Exception as e:
        print(e)


# done
@app.route("/updateGenerated", methods=['POST'])
def update_if_already_generated():
    try:
        story = StoryGenerated(request.get_json())
        print("update for " + str(story.generated_for))
        query = "update stories " \
                "set content = '" + _escape_specials(story.content) + "', regdate = current_timestamp " \
                "where ref = " + str(story.generated_for)
        with _get_connection() as conn:
            with conn.cursor() as cursor:
                cursor.execute(query)
                start_analyse(str(story.generated_for), str(story.content), str(story.fk_prompt))
                return {"message": "ok"}
    except Exception as e:
        print(e)


# done
@app.route("/redoAnalysisIfEmpty/")
def redo_analysis_if_empty():
    try:
        query = "select distinct s.ref, s.content, s.fk_prompt " \
                "from stories s " \
                "full join statsai sa on s.ref = sa.fk_story " \
                "inner join prompts p on s.fk_prompt = p.ref " \
                "where sa.fk_story is null " \
                "or sa.correctness is null " \
                "or sa.genreaccuracy is null " \
                "or sa.promptcompletion is null " \
                "or sa.vocabvariety is null " \
                "or sa.positiveness is null " \
                "or sa.negativeness is null " \
                "or sa.neutralness is null " \
                "or sa.genre is null "
        with _get_connection() as conn:
            with conn.cursor() as cursor:
                cursor.execute(query)
                rows = cursor.fetchall()
                for row in rows:
                    story_ref = row[0]
                    story_content = row[1]
                    fk_prompt = row[2]
                    query_delete = "delete from statsai where fk_story = " + str(story_ref)
                    cursor.execute(query_delete)
                    start_analyse(story_ref, story_content, fk_prompt)
                return {"message": "ok"}
    except Exception as e:
        print(e)


if __name__ == "__main__":
    app.run(host='0.0.0.0', port="5000", debug=True)
