@import ch.lucaro.hitls.api.SortJobManager
@import ch.lucaro.hitls.api.UserSession
@import ch.lucaro.hitls.api.UserSessionManager
@param SortJobManager jobManager
@param UserSessionManager sessionManager

<html lang="en">
<head>
    <title>Job Status</title>
    <link href="/bootstrap/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
    <script src="/bootstrap/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>

<body>
<main>
    <div class="container" style="padding-top: 5%">

        <table class="table table-striped">

            <thead>
            <tr>
                <th scope="col">Name</th>
                <th scope="col">Status</th>
                <th scope="col">Elements</th>
                <th scope="col">Votes</th>
                <th scope="col">Comparisons</th>
            </tr>
            </thead>
            <tbody>
            @for(String job : jobManager.allJobNames())
                <tr>
                    <td>${job}</td>
                    <td>${jobManager.getJob(job).getComplete() ? "complete" : "active"}</td>
                    <td>${jobManager.getJob(job).getElementCount()}</td>
                    <td>${jobManager.getStore(job).getVoteCount()}</td>
                    <td>${jobManager.getStore(job).getComparisonCount()}</td>
                </tr>
            @endfor
            </tbody>

        </table>

        <table class="table table-striped">

            <thead>
            <tr>
                <th scope="col">Session</th>
                <th scope="col">User id</th>
                <th scope="col">Votes</th>
                <th scope="col">Remaining</th>
                <th scope="col">Last vote</th>
                <th scope="col">mean time between votes</th>
                <th scope="col">Total session time</th>
            </tr>
            </thead>
            <tbody>
            @for(UserSession userSession : sessionManager.allSessions())
                <tr>
                    <td>${userSession.getSessionId()}</td>
                    <td>${userSession.getUserId()}</td>
                    <td>${userSession.getVoteCount()}</td>
                    <td>${userSession.getRemaining()}</td>
                    <td>${userSession.getLastVoteTimeFormatted()}</td>
                    <td>${userSession.getMeanVoteTime() / 1000}</td>
                    <td>${userSession.getTotalSessionTime() / 1000}</td>
                </tr>
            @endfor
            </tbody>

        </table>

    </div>

</main>

</body>
</html>