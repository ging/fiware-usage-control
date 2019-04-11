FROM python:3.8-rc-stretch

COPY requirements.txt generate-tickets.py ./
RUN pip install --no-cache-dir -r requirements.txt

#ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]
CMD ["python","./generate-tickets.py"]