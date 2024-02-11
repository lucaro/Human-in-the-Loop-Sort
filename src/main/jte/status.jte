@import ch.lucaro.hitls.api.SortJobManager
@param SortJobManager manager

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
            @for(String job : manager.allJobNames())
                <tr>
                    <td>${job}</td>
                    <td>${manager.getJob(job).getComplete() ? "complete" : "active"}</td>
                    <td>${manager.getJob(job).getElementCount()}</td>
                    <td>${manager.getStore(job).getVoteCount()}</td>
                    <td>${manager.getStore(job).getComparisonCount()}</td>
                </tr>
            @endfor
            </tbody>

        </table>

    </div>

</main>

</body>
</html>