import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/login')({
  component: RouteComponent,
})

function RouteComponent() {
  return (
    <div>
      <div className="card mx-auto mt-20 mb-20 w-full max-w-5xl shadow-xl">
        <div className="bg-base-100 grid grid-cols-1 rounded-xl md:grid-cols-2">
          {/* Left Hero Section */}
          <div>
            <div className="hero min-h-full rounded-l-xl bg-neutral-100">
              <div className="hero-content py-12">
                <div className="max-w-md text-center">
                  <img
                    src="/logo.svg"
                    className="mr-2 inline-block w-80"
                    alt="E-Annacarde logo"
                  />
                  <h1 className="mt-8 text-2xl font-bold">
                    Admin Dashboard Starter Kit
                  </h1>
                  <ul className="mt-4 space-y-2 text-left">
                    <li>
                      ✓ <span className="font-semibold">Light/dark</span> mode
                      toggle
                    </li>
                    <li>
                      ✓ <span className="font-semibold">Redux toolkit</span> and
                      other utility libraries configured
                    </li>
                    <li>
                      ✓{' '}
                      <span className="font-semibold">
                        Calendar, Modal, Sidebar
                      </span>{' '}
                      components
                    </li>
                    <li>
                      ✓ User-friendly{' '}
                      <span className="font-semibold">documentation</span>
                    </li>
                    <li>
                      ✓ <span className="font-semibold">Daisy UI</span>{' '}
                      components,{' '}
                      <span className="font-semibold">Tailwind CSS</span>{' '}
                      support
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>

          {/* Right Login Form */}
          <div className="px-10 py-24">
            <h2 className="mb-6 text-center text-2xl font-semibold">Login</h2>
            <form>
              <div className="space-y-6">
                <div className="w-full">
                  <label htmlFor="email" className="label">
                    Email Id
                  </label>
                  <input
                    id="email"
                    type="email"
                    placeholder=""
                    className="input w-full"
                  />
                </div>

                <div className="w-full">
                  <label htmlFor="password" className="label">
                    Password
                  </label>
                  <input
                    id="password"
                    type="password"
                    placeholder=""
                    className="input w-full"
                  />
                </div>
              </div>

              <div className="mt-2 text-right">
                <a
                  href="/forgot-password"
                  className="hover:text-primary text-sm transition duration-200 hover:underline"
                >
                  Forgot Password?
                </a>
              </div>

              <p className="text-error mt-8 text-center">
                {/* Error message placeholder */}
              </p>

              <button type="submit" className="btn btn-primary mt-6 w-full">
                Login
              </button>

              <div className="mt-4 text-center">
                Don't have an account yet?{' '}
                <a
                  href="/register"
                  className="hover:text-primary transition duration-200 hover:underline"
                >
                  Register
                </a>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}
