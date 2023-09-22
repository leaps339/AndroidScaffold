package com.leaps.scaffold

sealed class Result(code: Int)

class Success(code: Int, msg: String) : Result(code)

class Failed(code: Int, msg: String) : Result(code)