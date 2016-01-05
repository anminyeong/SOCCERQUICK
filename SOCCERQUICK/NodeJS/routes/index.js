var express = require('express');
var router = express.Router();
var async = require('async');
var request = require('request');
var gcm = require('node-gcm');
var fs = require('fs');

//mysql 연동
var mysql = require('mysql');

var connection = mysql.createConnection({
    host    :'localhost',
    port : 3306,
    user : 'root',
    password : '1234',
    database:'soccerq'
});


connection.connect(function(err) {
    if (err) {
        console.error('mysql connection error');
        console.error(err);
        throw err;
    } else {
        console.log('mysql connected');
    }
});

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/test',function(req, res, next){
	res.render('index', { title: 'hi' });
});


//구장 상세 정보 '/ground/detail/:ground_id'
////구장 리스트 '/ground/:local'
//매치 상세 정보 '/match/detail/:match_id'
//클럽 가입 신청 '/club/apply/:member_id/:club_id'
// 매치 신청 '/match/apply/:member_id/:applicant/:board_id'
//회원 가입 '/join'
//개인정보설정변경 '/set/:member_id' -get
//개인정보설정변경 '/set/:member_id' -post
//로그인 중복여부 확인 후 로그인 '/login/:member_id/:password'
//매치 글 보기 '/matchlist/:member_id/:match_people/:match_local/:match_date/:match_start/:match_end'
//게시판 글 목록 보기 '/board/:header'
///게시글 상세 정보 /board/detail/:board_id
//구단 게시판 작성  /club/board/upload/:member_id
//구단 게시글 '/club/board/:header/:club_name'
//게시판 글 등록 '/board/upload/:member_id'
//매치 글 등록 '/match/upload/:member_id/:club_name'
//매치 신청  /match_apply/:member_id/:applicant/:board_id
//소속 클럽 이름 '/clubinfo/:member_id'
//구단 창설 '/club/setup/:member_id'
//전체 구단 조회  '/clublist'
//자기 구단 조회 '/clublist/:member_id'



//게시판 글 삭제
router.post('/board/remove/:_id', function(req, res){
	var id = req.params._id;

	connection.query('DELETE FROM board WHERE _id=?',parseInt(id), function(err,r2){
				if(err){
					console.error(err);
					throw err;
				}else{
					res.send(200,"success");
				}
	});
});

//매치게시판 글 삭제
router.post('/matchboard/remove/:_id', function(req, res){
	var id = req.params._id;

	connection.query('DELETE FROM match_board WHERE _id=?',parseInt(id), function(err,r2){
				if(err){
					console.error(err);
					throw err;
				}else{
					res.send(200,"success");
				}
	});
});


//게시판 글 수정
router.post('/board/update/:_id', function(req, res){
	var id = req.params._id;

	var title = req.body.title;
	var content = req.body.content;
	var date = req.body.date;


	var para_header = req.body.header;
	var header = "";
	if(para_header == 0){
		header = "전체보기";
	}else if(para_header == 1){
		header = "중고거래";
	}else if(para_header == 2){
		header = "잡담";
	}else if(para_header == 3){
		header = "클럽원모집";
	}else if(para_header == 4){
		header = "정보공유";
	}


connection.query('update board set title="'+title+'", content="'+content+'", date="'+date+'", header="'+header+'" where _id=?',parseInt(id), function(err,r2){
				if(err){
					console.error(err);
					throw err;
				}else{
					res.send(200,"success");
				}
	});
});

//매치게시판 글 수정
router.post('/matchboard/update/:match_id', function(req, res){
		var match_id = req.params.match_id;
		console.log(match_id);
		var title = req.body.title;
		var content = req.body.content;
		var people = req.body.people;
		var local = req.body.local;
		var club_name = req.body.club_name;
		var match_date = req.body.match_date;
		var date = req.body.date;
		var start = req.body.start;
		var end = req.body.end;
		var stadium = req.body.stadium;

		console.log(typeof(date) + typeof(start) + typeof(end));
		// res.send(200,"success");

		connection.query('update match_board set title="'+title+'", content="'+content+'", persons="'+people+'", local="'+local+'", club_name="'+club_name+'", match_date="'+match_date+'", date="'+date+'", start_time="'+start+'", end_time="'+end+'", stadium_name="'+stadium+'" where _id=?',parseInt(match_id), function(err,r2){
					if(err){
						console.error(err);
						throw err;
					}else{
						res.send(200,"success");
					}
		});
});



//댓글 쓰기
router.post('/comment/upload/:board_id/:member_id', function(req, res){
	var board_id = req.params.board_id;
	var member_id = req.params.member_id;
	var date = req.body.date;
	var content = req.body.content;

	console.log({"board_id" : board_id, "member_id" : member_id, "date" : date, "content" : content});


				var comment_format = {
					"board_id" : board_id,
					"member_id" : member_id,
					"date" : date,
					"content" : content
				}
				connection.query('insert into board_comment set ?', comment_format, function(err,r2){
							if(err){
								console.error(err);
								throw err;
							}else{

								console.log(r2);
								res.send(200,"success");
							}
				});
});




//구단 가입신청 거부
router.post('/join_false/:member_id/:target_id/:club_id', function(req, res){
	var member_id = req.params.member_id;
	var target_id = req.params.target_id;
	var club_id = req.params.club_id;

	connection.query('select * from member where member_id=?', target_id, function(err,r1){
			if(err){
				console.error(err);
				throw err;
			}else{
					connection.query('DELETE FROM club_join WHERE member_id=? and club_id=?',[r1[0]._id,parseInt(club_id)], function(err,r2){
							if(err){
								console.error(err);
								throw err;
							}else{
												connection.query('select * from club_info where _id=?', club_id, function(err,r3){
															if(err){
																console.error(err);
																throw err;
															}else{
																	var meg = r3[0].club_name+' 클럽신청이 거절되었습니다.'
																	var message = new gcm.Message();
																	var message = new gcm.Message({
																	    collapseKey: 'demo',
																	    delayWhileIdle: true,
																	    timeToLive: 3,
																	    data: {
																	        title: 'SoccerQuick',
																	        message: meg,
																	        custom_key1: 'custom data1',
																	        custom_key2: 'custom data2'
																	    }
																	});

																	var server_api_key = 'AIzaSyDQ1OSG4GJUj9ElFRMOjLagJ86pll9jvqo';
																	var sender = new gcm.Sender(server_api_key);
																	var registrationIds = [];

																	var token = r1[0].token;
																	console.log(r1[0].token);
																	registrationIds.push(token);

																	sender.send(message, registrationIds, 4, function (err, result) {
																	    console.log(result);
																	});

																	res.send(200, "success");


															}
												});

								}
					})
			}
	});
});



//구단 가입 신청 승인
router.post('/join_true/:member_id/:target_id/:club_id', function(req, res){
	var member_id = req.params.member_id;
	var target_id = req.params.target_id;
	var club_id = req.params.club_id;



					connection.query('select * from club_info where _id=?',parseInt(club_id), function(err,r2){
							if(err){
								console.error(err);
								throw err;
							}else{
									connection.query('select * from member where member_id=?',target_id,function(err, r3){
												if(err){
													console.error(err);
													throw err;
												}else{
													connection.query('update club_join set accept=1 where member_id=? and club_id=?',[r3[0]._id, parseInt(club_id)], function(err,r1){
															if(err){
																console.error(err);
																throw err;
															}else{
													var meg = r2[0].club_name+' 클럽에 가입되셧습니다.'
													var message = new gcm.Message();
													var message = new gcm.Message({
													    collapseKey: 'dem o',
													    delayWhileIdle: true,
													    timeToLive: 3,
													    data: {
													        title: 'SoccerQuick',
													        message: meg,
													        custom_key1: 'custom data1',


													        custom_key2: 'custom data2'
													    }
													});

													var server_api_key = 'AIzaSyDQ1OSG4GJUj9ElFRMOjLagJ86pll9jvqo';
													var sender = new gcm.Sender(server_api_key);
													var registrationIds = [];

													var token = r3[0].token;
													console.log(r3[0].token);
													registrationIds.push(token);

													sender.send(message, registrationIds, 4, function (err, result) {
													    console.log(result);
													});

													res.send(200, "success");


												}
											})
										}//es
								});


							}
					})

});

//구단 상세 정보 보기
router.get('/club/detail/:member_id/:club_id', function(req, res){
	var member_id = req.params.member_id;
	var club_id = req.params.club_id;

	var str_club_id = 1;

	console.log(member_id);
	console.log("안오지:", typeof(club_id));
	var apply_nick = [];
	var apply_kakao = [];
	var apply_id = [];

	var member_nick = [];
	var member_kakao = [];
	str_club_id = parseInt(club_id);
	console.log(str_club_id, typeof(str_club_id));
	connection.query('select * from club_info where _id=?',str_club_id,function(err, r1){
			if(err){
				console.error(err);
				throw err;
			}else{

				  connection.query('select Count(member_id) as b from club_join where club_id=? and accept=1',str_club_id,function(err, r2){
									if(err){
										console.error(err);
										throw err;
									}else{

																	connection.query('select * from club_join where club_id=?', str_club_id, function(err, r4){
																		if(err){
																			console.error(err);
																			throw err;
																		}else{

																					async.eachSeries(r4, function(first, callback){

																							 if(first.accept == 0 ){
																							 	console.log("accept 0 이요" , first.accept);
																							 		connection.query('select * from member where _id=?',first.member_id,function(err, r5){
																							 					if(err){
																													console.error(err);
																													throw err;
																												}else if(r5.length < 1){
																													callback(null);
																												}
																												else{

																													apply_nick.push(r5[0].nickname);
																													apply_kakao.push(r5[0].kakao);
																													apply_id.push(r5[0].member_id);
																													// console.log(apply_nick + apply_kakao + apply_id)
																													callback(null);
																												}
																							 		});


																							 }else{
																							 		console.log("accept 1 이요" , first.accept);
																							 		connection.query('select * from member where _id=?',first.member_id,function(err, r6){
																							 					if(err){
																													console.error(err);
																													throw err;
																												}else if(r6.length < 1){
																													callback(null);
																												}else{
																													member_nick.push(r6[0].nickname);
																													member_kakao.push(r6[0].kakao);
																													console.log(member_nick)
																													callback(null);
																												}
																							 		});
																							 }

														                }, function(err){
														                    if(err){
														                        res.status(500).send({ ERROR: 'NOT FOUND CLUB' });
														                        return;
														                    }else{

																										var format = {
																											"title" : r1[0].club_name,
																											"img" : r1[0].logo,
																											"members" : r2[0].b,
																											"stadium" : r1[0].stadium_name,
																											"master" : r1[0].leader_id,
																											"content" : r1[0].club_content,
																											"member_nick" : member_nick,
																											"member_kakao" : member_kakao,
																											"apply_kakao" : apply_kakao,
																											"apply_nick" : apply_nick,
																											"apply_id" : apply_id
																										}

																										console.log(format);
																										res.send(200, {"club_info" : format});
														                    }
														                })

																		}
																	})

									}
					});


			}
	});
})





//구장 상세 정보
router.get('/ground/detail/:ground_id', function(req, res){
			var board_id = req.params.ground_id;

			console.log(board_id);

			connection.query('select * from stadium where _id=?', board_id, function(err, r1){
					if(err){
						console.error(err);
						throw err;
					}else{
						var jsonFormat = {
							"name" : r1[0].name,
							"address" : r1[0].address,
							"local" : r1[0].location,
							"num" : r1[0].phone,
							"time" : r1[0].time,
							"holiday" : r1[0].holiday,
							"fee" : r1[0].fee,
							"court" : r1[0].court,
							"parking" : r1[0].parking,
							"shower" : r1[0].shower,
							"market" : r1[0].snackbar,
							"mapwi" : r1[0].mapx,
							"mapkyung" : r1[0].mapy
						}

						console.log(jsonFormat);
						res.send(200,{"user_info" : jsonFormat});
					}
			});
});





//구장 리스트
router.get('/ground/:local', function(req, res){

			var local = req.params.local;
			console.log(local);
			var array_name = [];
			var array_court = [];
			var array_local = [];
			var array_ground_id = [];
			var array_phone = [];
			var array_img = [];

			 // 0-전체 보기, 1-유성구, 2-서구, 3-중구, 4-동구, 5-북구, 6-대덕구)
			var lovalv = "";
			if(local == 1){
				localv = 'select * from stadium where location="유성구"';
			}else if(local == 2){
				localv = 'select * from stadium where location="서구"';
			}else if(local == 3){
				localv = 'select * from stadium where location="중구"';
			}else if(local == 4){
				localv = 'select * from stadium where location="동구"';
			}else if(local == 5){
				localv = 'select * from stadium where location="북구"';
			}else if(local == 6){
				localv = 'select * from stadium where location="대덕구"';
			}else if(local == 0){
				localv = 'select * from stadium';
			}

			console.log("its this", localv);


			connection.query(localv, function(err, r1){
					if(err){
						console.error(err);
						throw err;
					}else{
									async.eachSeries(r1, function(first, callback){

										 try {
											    array_name.push(first.name);
													array_court.push(first.court);
													array_local.push(first.location);
													array_ground_id.push(first._id);
													array_phone.push(first.phone);
													array_img.push(first.img);

													callback(null);
											  } catch (e) {
											    console.error('catch error =========');
											    console.error(e);
											  }

	                }, function(err){
	                    if(err){
	                        res.status(500).send({ ERROR: 'NOT FOUND CLUB' });
	                        return;
	                    }else{
	                         var buf = {
															"name" : array_name,
															"court" : array_court,
															"local" : array_local,
															"ground_id" : array_ground_id,
															"num" : array_phone,
															'img' : array_img
													};

													console.log(buf);
													res.send(200, buf);
	                    }
	                })

					}
			});

});


//매치 상세 정보
router.get('/match/detail/:match_id', function(req, res){
		var match_id = req.params.match_id;

		var apply_id = [];
		var apply_team = [];
		var apply_kakao = [];
		var apply_logo = [];

		async.waterfall([
		function(callback) {

		connection.query('select * from match_board where _id=?',match_id, function(err, r1){
				if(err){
					console.error(err);
					throw err;
				}else{
					connection.query('select * from member where _id=?',r1[0].member_id, function(err, r5){
							if(err){
								console.error(err);
								throw err;
							}else{


												connection.query('select * from match_apply where match_id=?',r1[0]._id, function(err, r2){
													if(err){
														console.error(err);
														throw err;
													}else{
														console.log("r2 : ",r2);

																async.eachSeries(r2, function(first, callback){
									                    connection.query('select * from member where _id=?',first.member_id, function(err,r3){
																				if(err){
																					console.error(err);
																					throw err;
																				}else{


																							connection.query('select * from club_info where _id=?',first.club_id, function(err,r4){
																								if(err){
																									console.error(err);
																									throw err;
																								}else{

																											console.log("kakaooo",apply_kakao);
																											apply_id.push(r3[0].member_id);
																											apply_team.push(r4[0].club_name);
																											apply_kakao.push(r3[0].kakao);
																											apply_logo.push(r4[0].logo);

																											callback(null);


																								}
																							});



																				}
																			});
								                }, function(err){
								                    if(err){
								                        res.status(500).send({ ERROR: 'NOT FOUND CLUB' });
								                        return;
								                    }else{


								                    	var paraFormat = {
								                    						"board_id" : match_id,
																								"writer" : r5[0].member_id,
																								"title" : r1[0].title,
																								"date" : r1[0].date,
																								"match_date" : r1[0].match_date,
																								"start_time" : r1[0].start_time,
																								"end_time" : r1[0].end_time,
																								"person" : r1[0].persons,
																								"club" : r1[0].club_name,
																								"local" : r1[0].local,
																								"stadium" : r1[0].stadium_name,
																								"content" : r1[0].content,
																								"img" : r1[0].logo,
																								"accept" :r1[0].accept,
																								"member_id" : apply_id,
																								"member_team" : apply_team,
																								"kakao" : apply_kakao,
																								"member_logo" : apply_logo
																			}

																			console.log(paraFormat);
							                        callback(null, paraFormat);
								                    }
							                	}) //each seri
													}
												});
						}
					});
				}//else


		});

	}],function(err, result){
			console.log(result);
			res.json(200,result);
		});
});



//구단 신청
router.post('/club/apply/:member_id/:club_id', function(req,res){
		var member_id = req.params.member_id;
		var club_id = req.params.club_id;


		connection.query('select _id from member where member_id=?',member_id,function(err, r1){
				if(err){
					console.error(err);
					throw err;
				}else{


					connection.query('select * from club_info where _id=?',parseInt(club_id),function(err,r2){
								if(err){
									console.error(err);
									throw err;
								}else{

										connection.query('select * from member where member_id=?',r2[0].leader_id, function(err, r3){
															if(err){
																console.error(err);
																throw err;
															}else{

																var inser_format = {
																	'member_id' : r1[0]._id,
																	'club_id' : club_id
																}

																connection.query('insert into club_join set ?',inser_format, function(err,r4){
																			if(err){
																				console.error(err);
																				throw err;
																			}else{

																				var meg = member_id+'님이 클럽 가입을 요청했습니다.'
																				var message = new gcm.Message();
																				var message = new gcm.Message({
																				    collapseKey: 'demo',
																				    delayWhileIdle: true,
																				    timeToLive: 3,
																				    data: {
																				        title: 'SoccerQuick',
																				        message: meg,
																				        custom_key1: 'custom data1',


																				        custom_key2: 'custom data2'
																				    }
																				});

																				var server_api_key = 'AIzaSyDQ1OSG4GJUj9ElFRMOjLagJ86pll9jvqo';
																				var sender = new gcm.Sender(server_api_key);
																				var registrationIds = [];

																				var token = r3[0].token;
																				console.log(r3[0].token);
																				registrationIds.push(token);

																				sender.send(message, registrationIds, 4, function (err, result) {
																				    console.log(result);
																				});

																				res.send(200, "success");
																			}
																});




															}
										})



								}
					})



				}
		});

});


//가입된 구단 정보 요청
router.get('/groundinfo', function(req,res){
	var name_array = [];
		connection.query('select name from stadium', function(err, r1){
				if(err){
					console.error(err);
					throw err;
				}else{
					for(var i=0 ; i < r1.length; i++) {
								name_array.push(r1[i].name);
						}
						console.log( {"stadium_name" : name_array});
					res.send(200, {"stadium_name" : name_array});
				}
		});
});







//매치 글 등록
router.post('/match/upload/:member_id', function(req,res){
		var member_id = req.params.member_id;

		var club_name = req.body.club_name;
		var match_title = req.body.title;
		var match_content = req.body.content;
		var match_people = req.body.people;
		var match_local = req.body.local;
		var match_date = req.body.match_date;
		var startTime = req.body.start;
		var endTime = req.body.end;
		var date = req.body.date;
		var stadium = req.body.stadium;


		var paraFormat = {
			"club_name" : club_name,
			"match_title" : match_title,
			"match_content" : match_content,
			"match_people" : match_people,
			"match_local" : match_local,
			"match_date" : match_date,
			"startTime" : startTime,
			"endTime" : endTime,
			"date" : date,
			"stadium" : stadium
		}

		console.log(paraFormat);
		connection.query('select _id from member where member_id=?',member_id,function(err, r1){
				if(err){
					console.error(err);
					throw err;
				}else{
					connection.query('select logo from club_info where club_name=?',club_name,function(err, r2){
						if(err){
							console.error(err);
							throw err;
						}else{
								var postFormat = {
									"member_id" : r1[0]._id,
									"title" : match_title,
									"content" : match_content,
									"date" : date,
									"match_date" : match_date,
									"start_time" : startTime,
									"end_time" : endTime,
									"stadium_name" : stadium,
									"logo" : r2[0].logo,
									"local" : match_local,
									"club_name" : club_name,
									"persons" : match_people
								}
								console.log(postFormat);

								connection.query('insert into match_board set ?',postFormat,function(err, r2){
									if(err){
										console.error(err);
										throw err;
									}else{
										console.log(r2);
										console.log("match board insert success");
										res.send(200,"match board insert success");
									}
								});
						}
					});
				}
		});
});


//매치 신청
router.post('/match_apply/:member_id/:applicant/:board_id', function(req,res){
	var member_id = req.params.member_id;
	var applicant = req.params.applicant;
	var board_id = req.params.board_id;
	var club_id = "";



	var meg = applicant+'님이 매치 신청을 하셧습니다.'
	console.log(meg);
	var team_info = req.body.team;
				//글쓴이 아이디,   로그인 되있는 아이디     게시글 아이디     산텍힌 팀 정보
	console.log(member_id +" " + applicant + " " + board_id + " " + team_info);


	connection.query('select _id from club_info where club_name=?', team_info, function(err,r3){
					if(err){
						console.error(err);
						throw err;
					}else{
						connection.query('select * from member where member_id=?',member_id, function(err, r1){
					if(err){
						console.error(err);
						throw err;
					}else{
							connection.query('select _id from member where member_id=?', applicant, function(err,r4){
								if(err){
										console.error(err);
										throw err;
									}else{

													if(r3.length < 1){
														console.log(" 오프라인 매치 신청 ");
														club_id = null;
													}else{
														console.log(" 팀 매치 신청 ");
														club_id = r3[0]._id
													}



																var applySet = {
																	"match_id" : parseInt(board_id),
																	"club_id" : club_id,
																	"member_id" : r4[0]._id
																}

																console.log(applySet);

																connection.query('insert into match_apply set ?', applySet, function(err, r2){
																		if(err){
																			console.error(err);
																			throw err;
																		}else{
																			console.log('insert into match_apply set');
																			var message = new gcm.Message();
																				var message = new gcm.Message({
																				    collapseKey: 'demo',
																				    delayWhileIdle: true,
																				    timeToLive: 3,
																				    data: {
																				        title: 'SoccerQuick',
																				        message: meg,
																				        custom_key1: 'custom data1',


																				        custom_key2: 'custom data2'
																				    }
																				});

																				var server_api_key = 'AIzaSyDQ1OSG4GJUj9ElFRMOjLagJ86pll9jvqo';
																				var sender = new gcm.Sender(server_api_key);
																				var registrationIds = [];

																				var token = r1[0].token;
																				console.log(r1[0].token);
																				registrationIds.push(token);

																				sender.send(message, registrationIds, 4, function (err, result) {
																				    console.log(result);
																				});

																			res.send(200,"ok");

																		}
																});




									}
							})


					}
			});

					}
	});

});



//매치 성사 완료
router.post('/match_complete/:member_id/:applicant/:board_id', function(req,res){
	var member_id = req.params.member_id;
	var applicant = req.params.applicant;
	var board_id = req.params.board_id;
	var club_id = "";



	var meg = applicant+'님과 매치 성사 되었습니다.'
	console.log(meg);
	var team_info = req.body.team;
				//글쓴이 아이디,   로그인 되있는 아이디     게시글 아이디     산텍힌 팀 정보
	console.log(member_id +" " + applicant + " " + board_id + " " + team_info);



	connection.query('select * from member where member_id=?',member_id, function(err, r1){
				if(err){
					console.error(err);
					throw err;
				}else{

								 connection.query('update match_board set accept=1 where _id=?',board_id,function(err, r2){
								 		if(err){
								 			console.error(err);
								 			throw err;
								 		}else{


															var message = new gcm.Message();
															var message = new gcm.Message({
															    collapseKey: 'demo',
															    delayWhileIdle: true,
															    timeToLive: 3,
															    data: {
															        title: 'SoccerQuick',
															        message: meg,
															        custom_key1: 'custom data1',


															        custom_key2: 'custom data2'
															    }
															});

															var server_api_key = 'AIzaSyDQ1OSG4GJUj9ElFRMOjLagJ86pll9jvqo';
															var sender = new gcm.Sender(server_api_key);
															var registrationIds = [];

															var token = r1[0].token;
															console.log(r1[0].token);
															registrationIds.push(token);

															sender.send(message, registrationIds, 4, function (err, result) {
															    console.log(result);
															});

														res.send(200,"ok");

 										}
								  });


					}
			});

});


//회원 가입
router.post('/join', function(req, res){
	var member_id = req.body.member_id;
	var token = req.body.token;
	var user = {'member_id':member_id,
							'password':req.body.password,
							'nickname':req.body.nickname,
							'phone':req.body.phone,
							'kakao':req.body.kakao,
							'type':req.body.type,
							'token':token};


	console.log("device token !");
	console.log(token);

	connection.query('select member_id from member where member_id=?',member_id,function(err, r1){
		if(err){
			console.error(err);
			throw err;
		}else{
					if(r1 == 0){
							console.log("중복 회원 없음");
							connection.query('insert into member set ?',user,function(err, r2){
								if(err){
									console.error(err);
									throw err;
								}
									console.log('member insert : ' + req.body.name);
									res.send(200,0);	//0번이 성공적
							});
					}else{
							console.log("이미 존재하는 회원");
							res.send(201, 1);	//1번이 이미 존재하는 회원
					}
		}
	});
});

//회원 정보 수정
router.get('/set/:member_id', function(req, res){
	 var member_id = req.params.member_id;

	 connection.query('select * from member where member_id=?',member_id,function(err, r1){
	 		if(err){
	 			console.error(err);
	 			throw err;
	 		}else{
	 			console.log(r1);
	 			res.send(200,{"user_info":r1});
	 		}
	 });
});


//회원 정보 수정
router.post('/set/:member_id', function(req, res){
	 var member_id = req.params.member_id;

	 var type = req.body.type;
	 var nickname = req.body.nickname;
	 var phone = req.body.phone;
	 var kakao = req.body.kakao;

	 console.log(type);
	 console.log(nickname);
	 console.log(phone);
	 console.log(type);

	 connection.query('select password from member where member_id=?',member_id,function(err, r2){
	 			if(err){
	 				console.error(err);
	 			}else{
	 				 	  connection.query('update member set type="'+type+'", nickname="'+nickname+'", phone="'+phone+'", kakao="'+kakao+'", password="'+r2[0].password+'" where member_id=?',member_id,function(err, r1){
							 		if(err){
							 			console.error(err);
							 			throw err;
							 		}else{
							 			console.log(r1);
							 			res.send(200,"update success");
							 		}
						  });
	 			}
	 });

});


//로그인 중복여부 확인 후 로그인
router.get('/login/:member_id/:password', function(req, res){
		var member_id = req.params.member_id;
		var password = req.params.password;
		var checked = 0;


		connection.query('select member_id from member where member_id=? and password=?',[member_id ,password],function(err, r1){
				if(err){
					console.error(err);
					throw err;
				}
				try{
						if(r1[0].member_id != undefined && r1[0].member_id == member_id){
							console.log("등록된 회원");
							checked = 1;
							var form = { "check" : checked }
							res.send(200,form);
						}
				}catch (e){
					console.error('catch error ==========')
					console.error(e);
					checked = 0;
					var form = {
						"check" : checked
					}
					res.send(200, form);
				}
		});
});


//매치 글 보기
router.get('/matchlist/:member_id/:match_people/:match_local/:match_date/:match_start/:match_end', function(req, res){
		var member_id = req.params.member_id;
		var match_people = req.params.match_people;	//0 전체보기, 1 5vs5, 2 6vs6
		var match_local = req.params.match_local;
		var match_string = "";

		if(match_people == "1"){
			match_people = "5vs5"
		}else if(match_people == "2"){
			match_people = "6vs6"
		}

		if(match_local == "1"){
				match_string="유성구"
		}else if(match_local == "2"){
			match_string="서구"
		}else if(match_local == "3"){
			match_string ="중구"
		}else if(match_local == "4"){
			match_string="동구"
		}else if(match_local == "5"){
			match_string = "북구"
		}else if(match_local == "6"){
			match_string = "대덕구"
		}

		var match_date = req.params.match_date;
		var match_start = req.params.match_start;
		var match_end = req.params.match_end;

		console.log("member_id :"+member_id);
		console.log("match_people :"+match_people);
		console.log("match_local :"+match_local);
		console.log("match_date :"+match_date);
		console.log("match_start :"+match_start);
		console.log("match_end :"+match_end);

		//start, end 는 int 값으로 보내야 함
		var queryString = "select * from match_board where"

		if(match_people != "0"){
			queryString = queryString + " persons='"+match_people+"'";
		}if(match_people != "0" && match_local != "0"){
			queryString = queryString + " and local='"+match_string+"'";
		}if(match_people == "0" && match_local != "0"){
			queryString = queryString + " local='"+match_string+"'";
		}if(match_date != "1" && (match_people != "0" || match_local != "0")){
			queryString = queryString + " and match_date='"+match_date+"'";
		}if(match_date != "1" && (match_people == "0" && match_local == "0")){
			queryString = queryString + " match_date='"+match_date+"'";
		}if(match_start != "1" && (match_people != "0" || match_local != "0" || match_date != "1")){
			queryString = queryString + " and start_time="+match_start+"";
		}if(match_start != "1" && (match_people == "0" && match_local == "0" && match_date == "1")){
			queryString = queryString + " start_time="+match_start+"";
		}if(match_end != "1" && (match_people != "0" || match_local != "0" || match_date != "1" || match_start != "1")){
			queryString = queryString + " and end_time="+match_end+"";
		}if(match_end != "1" && (match_people == "0" && match_local == "0" && match_date == "1" && match_start =="1")){
			queryString = queryString + " end_time="+match_end+"";
		}if( match_people == "0" && match_local == "0" && match_date == "1" && match_start =="1" && match_end == "1"){
			queryString = "select * from match_board"
		}

		queryString = queryString + " order by _id desc";

		var array_title = [];
		var array_content = [];

		var array_id = [];
		var array_logo = [];
		var array_people = [];
		var array_local = [];
		var array_date = [];
		var array_start = [];
		var array_end = [];
		var array_title = [];
		var array_board_id = [];

		console.log("###", queryString);
			//match_date='2015-11-11'
			//"select * from match_board"

		connection.query(queryString, function(err, r1){
				if(err){
							console.error(err);
							throw err;
				}else if(r1.length < 1){
							console.log(r1.length);
							res.send(200, r1.length);
				}else{

								if(r1.length > 0){

									async.eachSeries(r1, function(first, callback){


										 try {
										 	connection.query('select * from member where _id=?',first.member_id, function(err, r2){
										 			if(err){
										 				console.error(err);
										 				throw err;
										 			}else{
										 				array_id.push(r2[0].member_id);
										 				array_board_id.push(first._id);
														array_title.push(first.title);
														array_local.push(first.local);
														array_people.push(first.persons);
														array_date.push(first.match_date);
														array_start.push(first.start_time);
														array_end.push(first.end_time);
														array_logo.push(first.logo);
														callback(null);
										 			}
										 	});

											  } catch (e) {
											    console.error('catch error =========');
											    console.error(e);
											  }

	                }, function(err){
	                    if(err){
	                        res.status(500).send({ ERROR: 'NOT FOUND CLUB' });
	                        return;
	                    }else{
	                         var buf = {
															"match_id" : array_board_id,
															"member_id" : array_id,
															"title" : array_title,
															"local" : array_local,
															"people" : array_people,
															"date" : array_date,
															"logo" : array_logo,
															"start" : array_start,
															"end" : array_end
													};

													console.log(buf);
													res.send(200, buf);
	                    }
	                })
							}else{
								res.send(200, "length 0");
							}
						} //else
				});
});



//게시판 글 목록 보기
router.get('/board/:board_head', function(req, res){
		var array_title = [];
		var array_writer = [];
		var array_header = [];
		var array_date = [];
		var array_board_id = [];


		var board_head = req.params.board_head;
		console.log(board_head);
		var queryString = 'select * from board order by _id desc'

		if(board_head == 1){
				queryString = 'select * from board where header="중고거래" order by _id desc';
		}else if(board_head == 2){
				queryString = 'select * from board where header="잡담" order by _id desc';
		}else if(board_head == 3){
			 queryString = 'select * from board where header="클럽원모집" order by _id desc';
		}else if(board_head == 4){
			queryString = 'select * from board where header="클럽원모집" order by _id desc';
		}



						connection.query(queryString, function(err, r1){
								if(err){
									console.error(err);
									throw err;
								}else{

										async.eachSeries(r1, function(first, callback){
										connection.query('select member_id from member where _id=?',first.member_id, function(err, r2){
											if(err){
												console.error(err);
												throw err;
											}else{

														array_title.push(first.title);
														array_writer.push(r2[0].member_id);
														array_header.push(first.header);
														array_date.push(first.date);
														array_board_id.push(first._id);


														callback(null);
											}
									});
									}, function(err){
	                    if(err){
	                        res.status(500).send({ ERROR: 'NOT FOUND CLUB' });
	                        return;
	                    }else{

														buf = {
																"title" : array_title,
																"writer" : array_writer,
																"header" : array_header,
																"date" : array_date,
																"board_id" : array_board_id
														};
														console.log(buf);
		                        res.send(200,buf);
	                    }
                	}) //each seri
							}
						});
});



//게시글 상세 정보
router.get('/board/detail/:board_id', function(req, res){
	var board_id = req.params.board_id;

	var array_commentID = [];
	var array_memberID = [];
	var array_date = [];
	var array_content = [];

	connection.query('select * from board where _id=?',board_id, function(err, r1){
			if(err){
				console.error(err);
				throw err;
			}else if(r1.length < 1){
				console.log([]);
				res.send(200,[]);
			}else{

				connection.query('select member_id from member where _id=?', r1[0].member_id, function(err, r2){
					if(err){
						console.error(err);
						throw err;
					}else{
						connection.query('select * from board_comment where board_id=? order by _id desc', board_id, function(err, r3){
								if(err){
									console.error(err);
									throw err;
								}else{

									for(var i=0 ; i < r3.length; i++) {
											array_commentID.push(r3[i]._id);
											array_memberID.push(r3[i].member_id);
											array_date.push(r3[i].date);
											array_content.push(r3[i].content);
									}

									var buf ={
										"member_id" : r2[0].member_id,
										"title" : r1[0].title,
										"content" : r1[0].content,
										"header" : r1[0].header,
										"date" : r1[0].date,
										"array_id" : array_commentID,
										"array_member_id" : array_memberID,
										"array_date" : array_date,
										"array_content" : array_content
									}
									console.log(buf);
									res.send(200,buf);
							}
					});

					}
				})
			}
	});
})

//구단 게시판 작성
router.post('/club/board/upload/:member_id', function(req, res){

	var member_id = req.params.member_id;

	connection.query('select * from member where member_id=?', member_id, function(err,r3){
				if(err){
					console.error(err);
					throw err;
				}else{
					var format = {
						"member_id" : r3[0]._id,
						"content" : req.body.title,
						"title" : req.body.content,
						"club_name" : req.body.team,
						"header" : req.body.header,
						"date" : req.body.date,
						"type" : req.body.type
					};

					connection.query('insert into board set ?',format,function(err, r2){
							if(err){
								console.error(err);
								throw err;
							}else{
								console.log(format);
								res.send(200,"success");
							}
					});

				}
	});




});


//클럽 게시판 글 확인
router.get('/club/board/:para', function(req,res){
		var board_head = req.params.para;
		var array_title = [];
		var array_writer = [];
		var array_header = [];
		var array_date = [];
		var array_board_id = [];
		var array_logo = [];

		var queryString = 'select * from board where type=2 order by _id desc';

		if(board_head == 1){
				queryString = 'select * from board where type=2 and header="중고거래" order by _id desc';
		}else if(board_head == 2){
				queryString = 'select * from board where type=2 and header="잡담" order by _id desc';
		}else if(board_head == 3){
			 queryString = 'select * from board where type=2 and header="클럽원모집" order by _id desc';
		}else if(board_head == 4){
			queryString = 'select * from board where type=2 and header="클럽원모집" order by _id desc';
		}


		connection.query(queryString, function(err, r1){

				if(err){
					console.error(err);
					throw err;
				}else{
							async.eachSeries(r1, function(first, callback){
                    connection.query('select member_id from member where _id=?',first.member_id,function(err,r3){
											if(err){
												console.error(err);
												throw err;
											}else{

													console.log(first.club_name);
												connection.query('select logo from club_info where club_name=?',first.club_name, function(err, r4){
													if(err){
															console.error(err);
															throw err;
													}
													else if(r4.length < 1){
															console.log(r4);
															res.send(201,"no value");
													}
														else{

																console.log(r3);
																array_title.push(first.title);
																array_writer.push(r3[0].member_id);
																array_header.push(first.header);
																array_date.push(first.date);
																array_board_id.push(first._id);
																array_logo.push(r4[0].logo);
																callback(null);
													}
												})

											}
										});
	                }, function(err){
	                    if(err){
	                        res.status(500).send({ ERROR: 'NOT FOUND CLUB' });
	                        return;
	                    }else{
	                    	var buf = {
	                    		"title" : array_title,
	                    		"writer" : array_writer,
	                    		"header" : array_header,
	                    		"date" : array_date,
	                    		"board_id" : array_board_id,
	                    		"logo" : array_logo
	                    	}

	                    	console.log(buf);
	                    	res.send(200,buf);
	                    	// callback(null);

	                    }
	                });

				}

		});
});




//게시판 글 등록
router.post('/board/upload/:member_id', function(req, res){
		var member_id = req.params.member_id;

		var para_header = req.body.header;
		var header = "";
		if(para_header == 0){
			header = "전체보기";
		}else if(para_header == 1){
			header = "중고거래";
		}else if(para_header == 2){
			header = "잡담";
		}else if(para_header == 3){
			header = "클럽원모집";
		}else if(para_header == 4){
			header = "정보공유";
		}

		console.log(member_id);

		connection.query('select _id from member where member_id=?',member_id, function(err, r1){
			if(err){
						console.error(err);
						throw err;
				}else{

					var text = {'member_id':r1[0]._id,
							'title':req.body.title,
							'date':req.body.date,
							'content':req.body.content,
							'type':req.body.type,
							'header': header};

					console.log(text);
					connection.query('insert into board set ?',text,function(err, result){
							if(err){
									console.error(err);
									throw err;
							}
							console.log('board insert : ' + result);
							res.send(200,'success');
					});
					}
		})
});



//소속 클럽 이름 보기
router.get('/clubinfo/:member_id', function(req, res){
		var member_id = req.params.member_id;
		var outputObject =[];
		outputObject.push("오프라인");

	async.waterfall([
		function(callback) {
		connection.query('select _id from member where member_id=?',member_id, function(err, r1){
				if(err){
						console.error(err);
						throw err;
				}else if(r1.length < 1){
						return;
				}else{
						connection.query('select club_id from club_join where member_id=?',r1[0]._id,function(err,r2){
							if(err){
								console.error(err);
								throw err;
							}else{


									async.eachSeries(r2, function(first, callback){
                    connection.query('select club_name from club_info where _id=?',first.club_id,function(err,r3){
											if(err){
												console.error(err);
												throw err;
											}else{
												outputObject.push(r3[0].club_name);
												callback(null);
											}
										});
	                }, function(err){
	                    if(err){
	                        res.status(500).send({ ERROR: 'NOT FOUND CLUB' });
	                        return;
	                    }else{
	                         callback(null, outputObject);
	                    }
	                })

							}
						});
				}
		});
	}],function(err, result){
			console.log(result);
			res.json(200,{"club_name":result});
		});
});


//구단 창설
router.post('/club/setup/:member_id', function(req, res){
		var member_id = req.params.member_id;
		var club_name = req.body.club_name;
		var club_content = req.body.club_content;
		var club_logo = req.body.logo;
		var stadium_name = req.body.stadium_id;	//stadium_name;

		var format = {
			"club_name":club_name,
			"logo":club_logo,
			"leader_id":member_id,
			"club_content":club_content,
			"stadium_name":stadium_name
		}

		console.log(format);

		connection.query('insert into club_info set ?',format,function(err, r2){
				if(err){
					console.error(err);
					throw err;
				}else{
					connection.query('select * from club_info where club_name=?', club_name, function(err,r1){
						if(err){
							console.error(err);
							throw err;
						}else{
							connection.query('select _id from member where member_id=?',member_id,function(err,r4){
									if(err){
										console.error(err);
										throw err;
									}else{
												var join_format ={
													"member_id" : r4[0]._id,
													"club_id" : r1[0]._id,
													"accept" : 1
												}
												connection.query('insert into club_join set ?', join_format ,function(err,r3){
													if(err){
														console.error(err);
														throw err;
													}else{
															console.log("insert club !");
															res.send(200,"success");
													}
												})
									}
							});


						}
					})

				}
		});
});

//전체 구단 조회
router.get('/clublist', function(req, res){
		var array_title = [];
		var array_content = [];
		var arrar_logo = [];
		var members = [];
		var recurit_array = []; //가입 받는걸 허락 하냐 ? 안하냐 ?
		var club_if_array = [];

		async.waterfall([
		function(callback) {
							connection.query('select * from club_info order by _id desc', function(err, r1){
									if(err){
										console.error(err);
										throw err;
									}else{

									async.eachSeries(r1, function(first, callback){
	                    connection.query('select Count(member_id) as b from club_join where club_id=? and accept=1',first._id,function(err, r2){
															if(err){
																console.error(err);
																throw err;
															}else{
																members.push(r2[0].b);
																console.log(r2[0].b);
																callback(null);
															}
											});
	                }, function(err){
	                    if(err){
	                        res.status(500).send({ ERROR: 'NOT FOUND CLUB' });
	                        return;
	                    }else{
	                    		for(var i=0 ; i < r1.length; i++) {
															array_title.push(r1[i].club_name);
															array_content.push(r1[i].club_content);
															arrar_logo.push(r1[i].logo);
															recurit_array.push(r1[i].recruit);
															club_if_array.push(r1[i]._id);
													}

													console.log(array_title);
													console.log(array_content);
													console.log(arrar_logo);
													console.log(members);
													var buf = {
															"title" : array_title,
															"content" : array_content,
															"img" : arrar_logo,
															"members" : members,
															"recruit" : recurit_array,
															"club_id" : club_if_array
													};


	                        callback(null, buf);
	                    }
	                })



							}
						});


	}],function(err, result){
			console.log(result);
			res.json(200,result);
		});
});



//자기 구단 조회
router.get('/clublist/:member_id', function(req, res){
	 	var member_id = req.params.member_id;
	  var array_title = [];
		var array_content = [];
		var arrar_logo = [];
		var members = [];
		var recurit_array = []; //가입 받는걸 허락 하냐 ? 안하냐 ?
		var club_if_array = [];

		console.log(member_id);

		async.waterfall([
		function(callback) {
						connection.query('select _id from member where member_id=?', member_id, function(err,r3){
								if(err){
										console.error(err);
										throw err;
									}else{
										connection.query('select club_id from club_join where member_id=? and accept=1', r3[0]._id,function(err, r1){
											if(err){
												console.error(err);
												throw err;
											}else{


									async.eachSeries(r1, function(first, callback){
										connection.query('select * from club_info where _id=?',first.club_id, function(err,r4){
												if(err){
													console.error(err);
													throw err;
												}else{
													connection.query('select Count(member_id) as b from club_join where club_id=? and accept=1',first.club_id,function(err, r2){
															if(err){
																console.error(err);
																throw err;
															}else{
																members.push(r2[0].b);
																console.log(r2[0].b);

															for(var i=0 ; i < r4.length; i++) {
																	array_title.push(r4[i].club_name);
																	array_content.push(r4[i].club_content);
																	arrar_logo.push(r4[i].logo);
																	recurit_array.push(r4[i].recruit);
																	club_if_array.push(r4[i]._id);
															}
															callback(null);
															}
													});

												}

										});

	                }, function(err){
	                    if(err){
	                        res.status(500).send({ ERROR: 'NOT FOUND CLUB' });
	                        return;
	                    }else{
	                    		var buf = {
															"title" : array_title,
															"content" : array_content,
															"img" : arrar_logo,
															"members" : members,
															"recruit" : recurit_array,
															"club_id" : club_if_array
													};
													console.log(buf);
													callback(null, buf);
	                    }
	                })
							}
						});
					}
		})
	}],function(err, result){
			console.log(result);
			res.send(200,result);
		});
});

//푸쉬 보내기 소오스

module.exports = router;
