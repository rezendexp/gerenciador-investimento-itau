# --- CONFIGURAÇÃO INICIAL ---
provider "aws" {
  region = "sa-east-1"
}

# --- 1. BANCO DE DADOS NOSQL (DYNAMODB) ---
resource "aws_dynamodb_table" "db" {
  name         = "tb_investimentos_processados"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "id"

  attribute {
    name = "id"
    type = "S"
  }
}

# --- 2. MENSAGERIA (SNS E SQS) ---
resource "aws_sns_topic" "investimento_topic" {
  name = "topico-investimentos"
}

resource "aws_sqs_queue" "investimento_queue" {
  name = "fila-investimentos"
}

# Conecta SNS -> SQS (Fan-out)
resource "aws_sns_topic_subscription" "sns_to_sqs" {
  topic_arn = aws_sns_topic.investimento_topic.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.investimento_queue.arn
}

# --- 3. IDENTIDADE E SEGURANÇA (IAM ROLE) ---
resource "aws_iam_role" "lambda_role" {
  name = "role-lambda-investimento"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = { Service = "lambda.amazonaws.com" }
    }]
  })
}

# Política de permissões para a Lambda
resource "aws_iam_policy" "lambda_policy" {
  name        = "lambda_policy_investimento"
  description = "Permite acesso ao DynamoDB, SQS e Logs"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:PutItem",
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ]
        Effect   = "Allow"
        Resource = "*"
      }
    ]
  })
}

# Anexa a política à Role
resource "aws_iam_role_policy_attachment" "lambda_logs" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = aws_iam_policy.lambda_policy.arn
}

# --- 4. FUNÇÃO LAMBDA EM PYTHON ---
resource "aws_lambda_function" "investimento_processor" {
  filename      = "lambda/lambda_function.zip"
  function_name = "processador-investimento-python"
  role          = aws_iam_role.lambda_role.arn
  handler       = "processor.lambda_handler"
  runtime       = "python3.11"

  depends_on = [aws_iam_role_policy_attachment.lambda_logs]
}

# --- 5. GATILHO (SQS -> LAMBDA) ---
resource "aws_lambda_event_source_mapping" "sqs_trigger" {
  event_source_arn = aws_sqs_queue.investimento_queue.arn
  function_name    = aws_lambda_function.investimento_processor.arn
}