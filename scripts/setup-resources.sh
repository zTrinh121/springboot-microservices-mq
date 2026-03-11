# Define ARNs
TOPIC_ARN="arn:aws:sns:us-east-1:000000000000:order-events-topic"
STOCK_QUEUE_ARN="arn:aws:sqs:us-east-1:000000000000:stock-queue"
EMAIL_QUEUE_ARN="arn:aws:sqs:us-east-1:000000000000:email-queue"

echo "Topic ARN: $TOPIC_ARN"
echo "Stock Queue ARN: $STOCK_QUEUE_ARN"
echo "Email Queue ARN: $EMAIL_QUEUE_ARN"
echo ""

# Subscribe stock queue
echo "Subscribing stock queue to SNS topic..."
python -m awscli --endpoint-url=http://localhost:4566 sns subscribe \
  --topic-arn $TOPIC_ARN \
  --protocol sqs \
  --notification-endpoint $STOCK_QUEUE_ARN \
  --attributes RawMessageDelivery=true

# Subscribe email queue
echo "Subscribing email queue to SNS topic..."
python -m awscli --endpoint-url=http://localhost:4566 sns subscribe \
  --topic-arn $TOPIC_ARN \
  --protocol sqs \
  --notification-endpoint $EMAIL_QUEUE_ARN \
  --attributes RawMessageDelivery=true

echo ""
echo "=== Verifying Subscriptions ==="
python -m awscli --endpoint-url=http://localhost:4566 sns list-subscriptions