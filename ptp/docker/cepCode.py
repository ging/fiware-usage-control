#!/usr/bin/env python

def createProgram(data):
  program = ""
  try:
    id = data["id"]
    policies = data["policies"]
  
    for idx, policy in enumerate(policies):
      rule = policy["rule"]
      ruleType = rule["type"] 
      ruleCode = ""
      punishment = policy["punishment"]["type"]
      stream = ""
      pattern = f"pattern{idx}"
      if (ruleType == "COUNT_POLICY"):
        stream = "entityStream"
        ruleCode = createCountRule(pattern,rule["params"]["numMaxEvents"], rule["params"]["eventWindow"])
      elif (ruleType == "AGGREGATION_POLICY"):
        stream = "operationStream"
        ruleCode = createAggregationRule(pattern, rule["params"]["aggregateTime"])
      else:
        continue
      if ("from" in policy or "to" in policy):
        froms = policy.get("from") or "00:00"
        tos = policy.get("to") or "24:00"
        within = "true" if policy.get("within") == None else str(policy.get("within")).lower()
        ruleCode += addTimeConstraints( froms, tos, within)
      program += ruleCode + "\n\n" + addPunishment(stream, pattern, ruleType, punishment) + "\n\n" 
    program += envExecute(id) 
  except:
    print("There was an error generating the CEP code")	
  
  return program

def createCountRule(val, numMaxEvents, facturationTime):
	return f"""val {val} = Pattern.begin[Entity]("events")
	.timesOrMore({numMaxEvents}+1)
	.within(Time.seconds({facturationTime}))"""

def createAggregationRule(val, aggregateTime):
	return f"""val {val} = Pattern.begin[ExecutionGraph]("start", AfterMatchSkipStrategy.skipPastLastEvent())
  	.where(Policies.executionGraphChecker(_, "source"))
  	.notFollowedBy("middle").where(Policies.executionGraphChecker(_, "aggregation",{aggregateTime}))
  	.followedBy("end").where(Policies.executionGraphChecker(_, "sink")).timesOrMore(1)"""

def addTimeConstraints(start = "00:00", end = "24:00", within = True):
	return f""".where(_=>Policies.checkTime("{start}","{end}", {within}))"""

def addPunishment(stream, pattern, rule, punishment):
	return f"""CEP.pattern({stream}, {pattern}).select(events =>
      Signals.createAlert(Policy.{rule}, events, Punishment.{punishment}))"""

def envExecute(id):
	return f"""env.execute("CEPMonitoring.{id}")"""