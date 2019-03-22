curl http://138.4.22.138:1026/v2/entities -s -S -H 'Content-Type: application/json' -d @- <<EOF
{	
        "id": "ticket","type": "ticket","_id": {"type": "String","value": 14},
	"mall": {"type": "String","value": 1},"date": {"type": "date","value": "01/14/2016"},
	"client": {"type": "int","value": 77014474650},"items": {"type": "object","value": {}}
}
EOF
