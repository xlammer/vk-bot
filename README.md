# vk-bot
Chat bot for VK social network that is able to perform simple tasks via plugins.

## How it works
You provide login and password that are used to authenticate the application allowing access to messages and other stuff.
Application listens to every event happening with specified account (currently only chat messages are handled).
Bot answers in private or group chat to specific commands.

## Currently supported features
1. Tubmlr - posts random picture from specified tumblr blog. `/t` command. 
2. Giphy - post random gif (or matching given keyword) from Giphy service. `/g <keyword>` command. 
3. Stickers - posts random sticker from owned and predefined collection. `/s` command.

## Setting up the bot

### Requirements
1. Register a [standalone application](https://vk.com/apps?act=manage) in VK
2. Install [Chrome driver](https://sites.google.com/a/chromium.org/chromedriver/downloads) and specify it's location in `application.properties` file.
3. Create `secret.properties` file with following content
```properties
bot.client.id=<application id given by vk when registering an app>
bot.secret.key=<secret key given by vk when registering an app>
vk.user.email=<bot's email or phone>
vk.user.password=<bot's password>
```
### Optional
1. Register Tumblr [app](https://www.tumblr.com/oauth/apps) to get secret and consumer keys and add tumblr blog.
2. Get Giphy [API key](https://github.com/Giphy/GiphyAPI) or use beta key **dc6zaTOxFJmzC**
2. Add following to the `secret.properties` file
```properties
tumblr.consumer.secret=<tumblr secret>
tumblr.consumer.key=<tumblr customer key>
tumblr.photo.source.blog=xxx.tumblr.com

giphy.api.key=<giphy api key>
```
