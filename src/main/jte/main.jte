@import ch.lucaro.hitls.api.PageState

@param PageState state

<html lang="en">
<head>
    <title>test</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL"
            crossorigin="anonymous"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>

<body>
<main>
    <div class="container" style="padding-top: 5%">

        @if(state.getPage() == PageState.Page.START)


            <h1 class="display-3 text-center">Welcome</h1>


            <p class="lead text-center">Welcome to this experiment. Some descriptive text goes here.</p>

            <p class="text-center"> some statement of what data is collected </p>
            <div class="row">

                <div class="col text-center">
                    <a href="/?consent=true" type="button" class="btn btn-success">I agree</a>
                </div>
                <div class="col text-center">
                    <a href="${state.getNoConsentLink()}" type="button" class="btn btn-danger">I do not agree</a>
                </div>

            </div>

        @elseif(state.getPage() == PageState.Page.COMPARE)
            compare
        @elseif(state.getPage() == PageState.Page.DONE)
            done
        @endif
    </div>

</main>

</body>

</html>