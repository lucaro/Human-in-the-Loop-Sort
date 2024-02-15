@import ch.lucaro.hitls.api.PageState
@import ch.lucaro.hitls.api.UserSession

@param UserSession session

<html lang="en">
<head>
    <title>Image Comparison Experiment</title>
    <link href="/bootstrap/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
    <script src="/bootstrap/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>

<body>
<main>

    <div class="container" style="padding-top: 5%">

        @if(session.getPageState().getPage() == PageState.Page.START)


            <h1 class="display-3 text-center">Welcome</h1>


            <p class="lead text-center">Welcome to this experiment. Some descriptive text goes here.</p>

            <p class="text-center"> some statement of what data is collected </p>
            <div class="row">

                <div class="col text-center">
                    <a href="/?consent=true" type="button" class="btn btn-success">I agree</a>
                </div>
                <div class="col text-center">
                    <a href="${session.getPageState().getNoConsentLink()}" type="button" class="btn btn-danger">I do not
                        agree</a>
                </div>

            </div>

        @elseif(session.getPageState().getPage() == PageState.Page.COMPARE)

            <div class="progress" role="progressbar" aria-label="Basic example" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
                <div class="progress-bar" style="width: ${session.getProgress()}%"></div>
            </div>


            <p class="lead text-center" style="padding-top: 100px">Which of the following two images would require more words to describe?</p>

            <div class="row">

                <div class="col">
                    <a href="/?o1=${session.next().getFirst().getId().toString()}&o2=${session.next().getSecond().getId().toString()}"><img
                                src="/img/${session.getSortJobName()}/${session.next().getFirst().getId().toString()}"
                                class="img-thumbnail mx-auto d-block" alt="image option 1"></a></div>
                <div class="col"><a
                   href="/?o1=${session.next().getSecond().getId().toString()}&o2=${session.next().getFirst().getId().toString()}"><img
                            src="/img/${session.getSortJobName()}/${session.next().getSecond().getId().toString()}"
                            class="img-thumbnail mx-auto d-block" alt="image option 2"></a>
                </div>

            </div>

        @elseif(session.getPageState().getPage() == PageState.Page.DONE)
            <p class="lead text-center">Thank you for participating, please click the button below to be redirected back to Prolific.</p>

        <div class="row">

            <div class="col text-center">
                <a href="${session.getPageState().getSuccessLink()}" type="button" class="btn btn-primary">Back to Prolific</a>
            </div>

            <meta http-equiv="refresh" content="5; URL=${session.getPageState().getSuccessLink()}">

        </div>

        @endif
    </div>

</main>

</body>

</html>