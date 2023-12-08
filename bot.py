# Replace 'YOUR_TOKEN' with your bot's token
TELEGRAM_TOKEN = '6902421568:AAHaVCxEOaUhFY1SaOKKoVuIs6odxtpBnZ8'
# Replace with the URL you want to post data to
TARGET_URL = 'http://localhost:8000/feature4/'

import logging
import requests
import json
from telegram import Update
from telegram.ext import filters, MessageHandler, ApplicationBuilder, CommandHandler, ContextTypes

logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    level=logging.INFO
)

async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await context.bot.send_message(chat_id=update.effective_chat.id, text="I'm a bot, please talk to me!")


async def echo(update: Update, context: ContextTypes.DEFAULT_TYPE):
    messages = update.message.text.split('\n')
    vendor = messages[0]
    menu = messages[1]
    oc = messages[-1]
    availability = None
    if oc == "open":
        availability = True
    if oc == "closed":
        availability = False
    data = {
    'vendor': vendor,
    'menu': menu,
    'availability':availability
    }  
    headers = {'Content-Type': 'application/json'}
    response = requests.post(TARGET_URL, data=json.dumps(data), headers=headers)
    print(response.text)
    await context.bot.send_message(chat_id=update.effective_chat.id, text="Done")

if __name__ == '__main__':
    application = ApplicationBuilder().token(TELEGRAM_TOKEN).build()
    echo_handler = MessageHandler(filters.TEXT & (~filters.COMMAND), echo)
    start_handler = CommandHandler('start', start)
    application.add_handler(start_handler)
    application.add_handler(echo_handler)
    
    application.run_polling()