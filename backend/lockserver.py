# -*- coding: utf-8 -*-
# lockserver.py
# Copyright (C) 2020 KunoiSayami
#
# This module is part of Workstation-Lock-By-Device and is released under
# the AGPL v3 License: https://www.gnu.org/licenses/agpl-3.0.txt
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program. If not, see <https://www.gnu.org/licenses/>.
import ctypes
import json
from configparser import ConfigParser

from aiohttp import web

routes = web.RouteTableDef()

auth_key = ''

rep = '''<html>
<head><title>403 Access Denied</title></head>
<body>
<center><h1>403 Access Denied</h1></center>
<hr><center>nginx</center>
</body>
</html>
<!-- a padding to disable MSIE and Chrome friendly error page -->
<!-- a padding to disable MSIE and Chrome friendly error page -->
<!-- a padding to disable MSIE and Chrome friendly error page -->
<!-- a padding to disable MSIE and Chrome friendly error page -->
<!-- a padding to disable MSIE and Chrome friendly error page -->
<!-- a padding to disable MSIE and Chrome friendly error page -->'''


@routes.get('/')
async def handle_get(_request: web.Request) -> web.Response:
    return web.Response(status=403, text=rep, content_type='text/html')

@routes.post('/post')
async def post_handler(request: web.Request) -> web.Response:
    try:
        j = json.loads(await request.text())
        if j.get('auth') == auth_key:
            ctypes.windll.user32.LockWorkStation()
            return web.Response(text='{"status": 200}')
    except:
        pass
    return web.Response(status=403)


if __name__ == "__main__":
    config = ConfigParser()
    config.read('config.ini')
    auth_key = config.get('http', 'key')
    app = web.Application()
    app.add_routes(routes)
    web.run_app(app, port=config.getint('http', 'port', fallback=8000))
