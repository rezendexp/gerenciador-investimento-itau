import json
import boto3

dynamodb = boto3.resource('dynamodb')
TABLE_NAME = "tb_investimentos_processados"

def lambda_handler(event, context):
    table = dynamodb.Table(TABLE_NAME)

    for record in event['Records']:
        try:
            full_body = json.loads(record['body'])
            # Se vier via SNS -> SQS, o dado real está em 'Message'
            if 'Message' in full_body:
                payload = json.loads(full_body['Message'])
            else:
                payload = full_body

            transacao_id = payload.get('id', 'ID_NAO_ENCONTRADO')
            cliente_id = payload.get('clienteId', 'DESCONHECIDO')
            valor = payload.get('valor', 0.0)

            print(f"Processando transação: {transacao_id}")

            table.put_item(Item={
                'id': str(transacao_id),
                'clienteId': cliente_id,
                'valor': str(valor),
                'status': 'FINALIZADO_PYTHON'
            })

        except Exception as e:
            print(f"Erro ao processar registro: {str(e)}")

    return {'statusCode': 200}