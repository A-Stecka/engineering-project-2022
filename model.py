from flask import json


class User:
    ref: int
    login: str
    password: str
    name: str
    profile_pic: int
    reg_date: str
    user_type: int
    email: str

    def __init__(self, body: json):
        if "ref" in body:
            self.ref = body["ref"]
        else:
            self.ref = -1
        self.login = body["login"]
        self.password = body["password"]
        self.name = body["name"]
        self.profile_pic = body["profile_pic"]
        self.reg_date = body["reg_date"]
        self.user_type = body["user_type"]
        self.email = body["email"]


class Story:
    title: str
    content: str
    reg_date: str
    fk_user: int
    fk_prompt: int

    def __init__(self, body: json):
        self.title = body["title"]
        self.content = body["content"]
        self.reg_date = body["reg_date"]
        self.fk_user = body["fk_user"]
        self.fk_prompt = body["fk_prompt"]


class StoryGenerated:
    content: str
    generated_for: int
    fk_user: int
    fk_prompt: int

    def __init__(self, body: json):
        self.content = body["content"]
        self.fk_prompt = body["fk_prompt"]
        self.fk_user = body["fk_user"]
        self.generated_for = body["generatedfor"]


class Comment:
    content: str
    reg_date: str
    fk_user: int
    fk_story: int

    def __init__(self, body: json):
        self.content = body["content"]
        self.reg_date = body["reg_date"]
        self.fk_user = body["fk_user"]
        self.fk_story = body["fk_story"]


class NewPassword:
    user_ref: int
    password: str

    def __init__(self, body: json):
        self.user_ref = body["ref"]
        self.password = body["password"]


class NewName:
    user_ref: int
    name: str

    def __init__(self, body: json):
        self.user_ref = body["ref"]
        self.name = body["name"]


class NewProfilePic:
    user_ref: int
    profile_pic: int

    def __init__(self, body: json):
        self.user_ref = body["ref"]
        self.profile_pic = body["profile_pic"]


class Score:
    value: float
    reg_date: str
    fk_story: int
    fk_user: int

    def __init__(self, body: json):
        self.value = body["value"]
        self.reg_date = body["reg_date"]
        self.fk_story = body["fk_story"]
        self.fk_user = body["fk_user"]


class MinigameAnswer:
    correct: float
    reg_date: str
    fk_story: int
    fk_user: int

    def __init__(self, body: json):
        self.correct = body["correct"]
        self.reg_date = body["reg_date"]
        self.fk_story = body["fk_story"]
        self.fk_user = body["fk_user"]
