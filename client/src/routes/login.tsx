import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/login")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div>
      <div className="card mx-auto w-full max-w-5xl  shadow-xl mt-20 mb-20">
        <div className="grid  md:grid-cols-2 grid-cols-1  bg-base-100 rounded-xl">
          <div className="">
            <div className="hero min-h-full rounded-l-xl bg-neutral-100">
              <div className="hero-content py-12">
                <div className="max-w-md">
                  <h1 className="text-3xl text-center font-bold ">
                    <img
                      src="/logo.svg"
                      className="w-80 inline-block mr-2"
                      alt="E-Annacarde logo"
                    />
                  </h1>

                  <h1 className="text-2xl mt-8 font-bold">
                    Admin Dashboard Starter Kit
                  </h1>
                  <p className="py-2 mt-4">
                    ✓ <span className="font-semibold">Light/dark</span> mode
                    toggle
                  </p>
                  <p className="py-2 ">
                    ✓ <span className="font-semibold">Redux toolkit</span> and
                    other utility libraries configured
                  </p>
                  <p className="py-2">
                    ✓{" "}
                    <span className="font-semibold">
                      Calendar, Modal, Sidebar{" "}
                    </span>{" "}
                    components
                  </p>
                  <p className="py-2  ">
                    ✓ User-friendly{" "}
                    <span className="font-semibold">documentation</span>
                  </p>
                  <p className="py-2  mb-4">
                    ✓ <span className="font-semibold">Daisy UI</span>{" "}
                    components,{" "}
                    <span className="font-semibold">Tailwind CSS</span> support
                  </p>
                </div>
              </div>
            </div>
          </div>
          <div className="py-24 px-10">
            <h2 className="text-2xl font-semibold mb-2 text-center">Login</h2>
            <form>
              <div className="mb-4">
                <div className="form-control w-full mt-4">
                  <label className="label">
                    <span className="label-text text-base-content undefined">
                      Email Id
                    </span>
                  </label>
                  <input
                    type="emailId"
                    placeholder=""
                    className="input  input-bordered w-full "
                    value=""
                  />
                </div>
                <div className="form-control w-full mt-4">
                  <label className="label">
                    <span className="label-text text-base-content undefined">
                      Password
                    </span>
                  </label>
                  <input
                    type="password"
                    placeholder=""
                    className="input  input-bordered w-full "
                    value=""
                  />
                </div>
              </div>
              <div className="text-right text-primary">
                <a href="/forgot-password">
                  <span className="text-sm  inline-block  hover:text-primary hover:underline hover:cursor-pointer transition duration-200">
                    Forgot Password?
                  </span>
                </a>
              </div>
              <p className="text-center  text-error mt-8"></p>
              <button type="submit" className="btn mt-2 w-full btn-primary">
                Login
              </button>
              <div className="text-center mt-4">
                Don't have an account yet?{" "}
                <a href="/register">
                  <span className="  inline-block  hover:text-primary hover:underline hover:cursor-pointer transition duration-200">
                    Register
                  </span>
                </a>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
